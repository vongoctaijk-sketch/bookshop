import { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Button, Card, DatePicker, Form, Input, InputNumber, Modal, Select, Switch, Table, message } from "antd";
import dayjs from "dayjs";
import khuyenMaiService from "../../services/KhuyenMaiservice";

const money = (value) => Number(value || 0).toLocaleString("vi-VN", { style: "currency", currency: "VND" });

export default function PromotionPage() {
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();
  const client = useQueryClient();
  const query = useQuery({ queryKey: ["promotions"], queryFn: khuyenMaiService.getAll });
  const promotions = Array.isArray(query.data) ? query.data : [];
  const close = () => { setOpen(false); setEditing(null); form.resetFields(); };
  const edit = (record = null) => {
    setEditing(record);
    setOpen(true);
    form.setFieldsValue(record ? { ...record, dates: [dayjs(record.ngayBatDau), dayjs(record.ngayKetThuc)] } : {
      loaiGiam: "PERCENT", donHangToiThieu: 0, soLuong: 1, dangHoatDong: true,
      dates: [dayjs(), dayjs().add(7, "day")],
    });
  };
  const save = async (values) => {
    const { dates, ...data } = values;
    const payload = { ...data, maKhuyenMai: data.maKhuyenMai.trim().toUpperCase(), ngayBatDau: dates[0].format("YYYY-MM-DD"), ngayKetThuc: dates[1].format("YYYY-MM-DD") };
    try {
      if (editing) await khuyenMaiService.update(editing.id, payload);
      else await khuyenMaiService.create(payload);
      message.success("Đã lưu chương trình khuyến mãi");
      close();
      await client.invalidateQueries({ queryKey: ["promotions"] });
    } catch (error) {
      console.error("Save promotion error:", error);
      message.error(error.response?.data?.message || "Không thể lưu khuyến mãi");
    }
  };
  const remove = (record) => Modal.confirm({
    title: "Xóa chương trình khuyến mãi?",
    onOk: async () => {
      try { await khuyenMaiService.delete(record.id); await client.invalidateQueries({ queryKey: ["promotions"] }); message.success("Đã xóa khuyến mãi"); }
      catch (error) { console.error("Delete promotion error:", error); message.error("Không thể xóa khuyến mãi"); }
    },
  });
  const columns = [
    { title: "Mã", dataIndex: "maKhuyenMai", key: "code" },
    { title: "Chương trình", dataIndex: "tenKhuyenMai", key: "name" },
    { title: "Mức giảm", key: "discount", render: (_, row) => row.loaiGiam === "PERCENT" ? `${row.giaTri}%` : money(row.giaTri) },
    { title: "Thời gian", key: "dates", render: (_, row) => `${row.ngayBatDau} - ${row.ngayKetThuc}` },
    { title: "Lượt còn lại", dataIndex: "soLuong", key: "quantity" },
    { title: "Trạng thái", dataIndex: "dangHoatDong", key: "active", render: (value) => value ? "Đang chạy" : "Tạm dừng" },
    { title: "Thao tác", key: "actions", render: (_, row) => <><Button size="small" onClick={() => edit(row)}>Sửa</Button> <Button size="small" danger onClick={() => remove(row)}>Xóa</Button></> },
  ];
  return (
    <Card title="Khuyến mãi" extra={<Button type="primary" onClick={() => edit()}>Tạo khuyến mãi</Button>}>
      <Table rowKey="id" loading={query.isLoading} columns={columns} dataSource={promotions} pagination={{ pageSize: 10 }} />
      <Modal title={editing ? "Cập nhật khuyến mãi" : "Tạo khuyến mãi"} open={open} onCancel={close} onOk={() => form.submit()} okText="Lưu">
        <Form form={form} layout="vertical" onFinish={save}>
          <Form.Item name="maKhuyenMai" label="Mã khuyến mãi" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="tenKhuyenMai" label="Tên chương trình" rules={[{ required: true }]}><Input /></Form.Item>
          <div className="grid grid-cols-2 gap-4">
            <Form.Item name="loaiGiam" label="Loại giảm" rules={[{ required: true }]}><Select options={[{ value: "PERCENT", label: "Phần trăm" }, { value: "FIXED", label: "Số tiền" }]} /></Form.Item>
            <Form.Item name="giaTri" label="Giá trị giảm" rules={[{ required: true }]}><InputNumber min={0.01} className="w-full" /></Form.Item>
          </div>
          <Form.Item name="donHangToiThieu" label="Đơn tối thiểu"><InputNumber min={0} className="w-full" /></Form.Item>
          <Form.Item name="dates" label="Thời gian áp dụng" rules={[{ required: true }]}><DatePicker.RangePicker className="w-full" /></Form.Item>
          <Form.Item name="soLuong" label="Số lượt phát hành" rules={[{ required: true }]}><InputNumber min={1} className="w-full" /></Form.Item>
          <Form.Item name="dangHoatDong" label="Đang hoạt động" valuePropName="checked"><Switch /></Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
