import { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Button, Card, InputNumber, Modal, Space, Table, Tag, message } from "antd";
import khachHangService from "../../services/KhachHangservice";

const getCustomers = async () => {
  const response = await khachHangService.getAll();
  return Array.isArray(response) ? response : [];
};
const money = (value) => Number(value || 0).toLocaleString("vi-VN", { style: "currency", currency: "VND" });

export default function CustomerPage() {
  const [selected, setSelected] = useState(null);
  const [points, setPoints] = useState(0);
  const client = useQueryClient();
  const query = useQuery({ queryKey: ["dashboard", "customers"], queryFn: getCustomers });

  const updatePoints = async () => {
    try {
      await khachHangService.update(selected.id, { diemTichLuy: points });
      message.success("Đã cập nhật điểm và hạng thành viên");
      setSelected(null);
      await client.invalidateQueries({ queryKey: ["dashboard", "customers"] });
    } catch (error) {
      console.error("Update customer membership error:", error);
      message.error(error.response?.data?.message || "Không thể cập nhật thành viên");
    }
  };
  const columns = [
    { title: "Khách hàng", dataIndex: "hoTen", key: "name" },
    { title: "Email", dataIndex: "email", key: "email" },
    { title: "Điện thoại", dataIndex: "sdt", key: "phone" },
    { title: "Đơn hoàn thành", dataIndex: "soDonHoanThanh", key: "orders" },
    { title: "Chi tiêu", dataIndex: "tongChiTieu", key: "spend", render: money },
    { title: "Điểm", dataIndex: "diemTichLuy", key: "points" },
    { title: "Hạng", dataIndex: "hangThanhVien", key: "tier", render: (tier, record) => <Tag color={record.vip ? "gold" : "blue"}>{record.vip ? "VIP" : tier}</Tag> },
    { title: "Chăm sóc", key: "care", render: (_, customer) => <Space><Button size="small" onClick={() => { setSelected(customer); setPoints(customer.diemTichLuy || 0); }}>Điều chỉnh điểm</Button>{customer.sdt && <Button size="small" href={`tel:${customer.sdt}`}>Gọi khách</Button>}</Space> },
  ];
  return (
    <Card title="Khách hàng & thành viên">
      <Table rowKey="id" loading={query.isLoading} dataSource={query.data || []} columns={columns} pagination={{ pageSize: 10 }} />
      <Modal title={`Điều chỉnh điểm - ${selected?.hoTen || ""}`} open={Boolean(selected)} onCancel={() => setSelected(null)} onOk={updatePoints} okText="Lưu">
        <InputNumber min={0} value={points} onChange={(value) => setPoints(value || 0)} className="w-full" addonAfter="điểm" />
        <p className="mt-3 text-gray-500">Hạng tự động: Đồng dưới 100, Bạc từ 100, Vàng từ 500, VIP từ 1.000 điểm.</p>
      </Modal>
    </Card>
  );
}
