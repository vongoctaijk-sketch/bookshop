import { useMemo, useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import {
  Button,
  Card,
  DatePicker,
  Form,
  InputNumber,
  Modal,
  Select,
  Space,
  Table,
  Tag,
  Statistic,
  message,
} from "antd";
import { Plus, X } from "lucide-react";
import dayjs from "dayjs";
import phieuNhapService from "../../services/PhieuNhapservice";
import sachService from "../../services/Sachservice";
import nhaCungCapService from "../../services/NhaCungCapservice";
import { useAuthState } from "../../hooks/useAuth";

const money = (value) => Number(value || 0).toLocaleString("vi-VN", { style: "currency", currency: "VND" });
const list = (response) => Array.isArray(response) ? response : response?.content || [];

export default function PurchasePage() {
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [range, setRange] = useState([dayjs().startOf("month"), dayjs()]);
  const [form] = Form.useForm();
  const queryClient = useQueryClient();
  const { user } = useAuthState();
  const purchases = useQuery({ queryKey: ["purchases"], queryFn: phieuNhapService.getAll });
  const books = useQuery({ queryKey: ["dashboard", "books"], queryFn: sachService.getAll });
  const suppliers = useQuery({ queryKey: ["dashboard", "suppliers"], queryFn: nhaCungCapService.getAll });
  const bookOptions = useMemo(() => list(books.data).map((book) => ({
    value: book.id,
    label: `${book.tenSach} (tồn: ${book.soLuongTon})`,
  })), [books.data]);
  const supplierOptions = list(suppliers.data).map((supplier) => ({
    value: supplier.id, label: supplier.tenNcc,
  }));
  const rows = list(purchases.data);
  const filteredRows = rows.filter((row) => {
    const date = dayjs(row.ngayNhap);
    return date.isValid() && !date.isBefore(range[0], "day") && !date.isAfter(range[1], "day");
  });
  const completedRows = filteredRows.filter((row) => row.trangThai !== "HUY");
  const totalValue = completedRows.reduce((sum, row) => sum + Number(row.tongTien || 0), 0);

  const openForm = (record = null) => {
    setEditing(record);
    setOpen(true);
    form.setFieldsValue(record ? {
      nhaCungCapId: record.nhaCungCap?.id,
      items: (record.chiTietPhieuNhaps || []).map((item) => ({
        sachId: item.sach?.id,
        soLuong: item.soLuong,
        giaNhap: Number(item.giaNhap),
      })),
    } : { items: [{ soLuong: 1, giaNhap: 0 }] });
  };

  const save = async (values) => {
    try {
      const payload = {
        nhaCungCapId: values.nhaCungCapId,
        nguoiDungId: user?.id,
        chiTietPhieuNhaps: values.items.map(({ sachId, soLuong, giaNhap }) => ({ sachId, soLuong, giaNhap })),
      };
      if (editing) await phieuNhapService.update(editing.id, payload);
      else await phieuNhapService.create(payload);
      message.success(editing ? "Đã cập nhật phiếu nhập và tồn kho" : "Tạo phiếu nhập và cập nhật tồn kho thành công");
      setOpen(false);
      setEditing(null);
      form.resetFields();
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["purchases"] }),
        queryClient.invalidateQueries({ queryKey: ["dashboard", "books"] }),
        queryClient.invalidateQueries({ queryKey: ["dashboard"] }),
      ]);
    } catch (error) {
      console.error("Create purchase error:", error);
      message.error(error.response?.data?.message || "Không thể tạo phiếu nhập");
    }
  };

  const cancel = async (record) => {
    try {
      await phieuNhapService.cancel(record.id);
      message.success("Đã hủy phiếu nhập và hoàn tồn kho");
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["purchases"] }),
        queryClient.invalidateQueries({ queryKey: ["dashboard", "books"] }),
      ]);
    } catch (error) {
      console.error("Cancel purchase error:", error);
      message.error(error.response?.data?.message || "Không thể hủy phiếu nhập");
    }
  };

  const columns = [
    { title: "Mã phiếu", dataIndex: "id", key: "id", render: (id) => `PN-${id}` },
    { title: "Ngày nhập", dataIndex: "ngayNhap", key: "ngayNhap", render: (value) => value ? new Date(value).toLocaleString("vi-VN") : "-" },
    { title: "Nhà cung cấp", dataIndex: ["nhaCungCap", "tenNcc"], key: "supplier" },
    { title: "Số đầu sách", dataIndex: "chiTietPhieuNhaps", key: "count", render: (value) => value?.length || 0 },
    { title: "Tổng tiền", dataIndex: "tongTien", key: "total", render: money },
    { title: "Trạng thái", dataIndex: "trangThai", key: "state", render: (value) => <Tag color={value === "HUY" ? "red" : "green"}>{value === "HUY" ? "Đã hủy" : "Hoàn thành"}</Tag> },
    { title: "Thao tác", key: "actions", render: (_, row) => row.trangThai !== "HUY" && <Space><Button size="small" onClick={() => openForm(row)}>Sửa</Button><Button danger size="small" icon={<X size={14} />} onClick={() => Modal.confirm({ title: "Hủy phiếu nhập?", content: "Số lượng đã nhập sẽ được trừ lại khỏi tồn kho.", okText: "Hủy phiếu", cancelText: "Giữ phiếu", onOk: () => cancel(row) })}>Hủy phiếu</Button></Space> },
  ];

  return (
    <Card title="Quản lý nhập hàng" extra={<Button type="primary" icon={<Plus size={16} />} onClick={() => openForm()}>Tạo phiếu nhập</Button>}>
      <div className="mb-4 flex flex-wrap items-center justify-between gap-4">
        <DatePicker.RangePicker value={range} onChange={(value) => value && setRange(value)} />
        <div className="grid grid-cols-2 gap-6">
          <Statistic title="Số phiếu hoàn thành" value={completedRows.length} />
          <Statistic title="Tổng tiền nhập trong kỳ" value={totalValue} formatter={(value) => money(value)} />
        </div>
      </div>
      <Table rowKey="id" loading={purchases.isLoading} dataSource={filteredRows} columns={columns} expandable={{ expandedRowRender: (record) => <Table size="small" rowKey="id" pagination={false} dataSource={record.chiTietPhieuNhaps || []} columns={[
        { title: "Sách", dataIndex: ["sach", "tenSach"], key: "book" },
        { title: "Số lượng", dataIndex: "soLuong", key: "quantity" },
        { title: "Giá nhập", dataIndex: "giaNhap", key: "price", render: money },
        { title: "Thành tiền", key: "total", render: (_, item) => money(Number(item.giaNhap) * Number(item.soLuong)) },
      ]} /> }} />
      <Modal title={editing ? `Cập nhật phiếu PN-${editing.id}` : "Tạo phiếu nhập"} open={open} onCancel={() => { setOpen(false); setEditing(null); form.resetFields(); }} onOk={() => form.submit()} okText="Lưu phiếu">
        <Form form={form} layout="vertical" onFinish={save} initialValues={{ items: [{ soLuong: 1, giaNhap: 0 }] }}>
          <Form.Item name="nhaCungCapId" label="Nhà cung cấp" rules={[{ required: true, message: "Chọn nhà cung cấp" }]}>
            <Select showSearch optionFilterProp="label" options={supplierOptions} placeholder="Chọn nhà cung cấp" />
          </Form.Item>
          <Form.List name="items" rules={[{ validator: async (_, values) => { if (!values?.length) throw new Error("Phiếu cần ít nhất một đầu sách"); } }]}>
            {(fields, { add, remove }) => (
              <div className="space-y-3">
                {fields.map((field) => (
                  <Space key={field.key} align="start" className="flex w-full">
                    <Form.Item {...field} name={[field.name, "sachId"]} label="Sách" rules={[{ required: true }]}><Select showSearch optionFilterProp="label" options={bookOptions} className="min-w-52" /></Form.Item>
                    <Form.Item {...field} name={[field.name, "soLuong"]} label="Số lượng" rules={[{ required: true }]}><InputNumber min={1} /></Form.Item>
                    <Form.Item {...field} name={[field.name, "giaNhap"]} label="Giá nhập" rules={[{ required: true }]}><InputNumber min={0} /></Form.Item>
                    <Button danger className="mt-8" icon={<X size={14} />} onClick={() => remove(field.name)} aria-label="Xóa sách" />
                  </Space>
                ))}
                <Button onClick={() => add({ soLuong: 1, giaNhap: 0 })} icon={<Plus size={14} />}>Thêm sách</Button>
              </div>
            )}
          </Form.List>
        </Form>
      </Modal>
    </Card>
  );
}
