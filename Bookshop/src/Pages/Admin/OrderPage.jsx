import React, { useEffect, useState } from "react";
import { Card, Table, Tag, Select, message, Space, Button, Modal } from "antd";
import hoaDonService from "../../services/Hoadonservice";

const statusOptions = [
  { value: "PENDING", label: "Chờ xử lý" },
  { value: "CONFIRMED", label: "Đã xác nhận" },
  { value: "SHIPPING", label: "Đang giao" },
  { value: "COMPLETED", label: "Hoàn thành" },
  { value: "CANCELLED", label: "Đã hủy" },
];

const statusColor = {
  PENDING: "gold",
  CONFIRMED: "blue",
  SHIPPING: "cyan",
  COMPLETED: "green",
  CANCELLED: "red",
};

function OrderPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const payload = await hoaDonService.getAll();
      setOrders(Array.isArray(payload) ? payload : []);
    } catch (error) {
      console.error("Error fetching orders:", error);
      message.error("Không thể tải danh sách đơn hàng");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleStatusChange = async (orderId, nextStatus) => {
    try {
      await hoaDonService.update(orderId, { trangThai: nextStatus });
      message.success("Cập nhật trạng thái thành công");
      fetchOrders();
    } catch (error) {
      console.error("Update status error:", error);
      message.error("Cập nhật trạng thái thất bại");
    }
  };

  const handleOpenOrderDetail = (record) => {
    setSelectedOrder(record);
    setDetailModalOpen(true);
  };

  const columns = [
    {
      title: "Mã hóa đơn",
      dataIndex: "maHoaDon",
      key: "maHoaDon",
    },
    {
      title: "Khách hàng",
      dataIndex: "khachHang",
      key: "khachHang",
      render: (khachHang) => khachHang?.hoTen || "Khách vãng lai",
    },
    {
      title: "Ngày bán",
      dataIndex: "ngayBan",
      key: "ngayBan",
      render: (value) => new Date(value).toLocaleString("vi-VN"),
    },
    {
      title: "Tổng tiền",
      dataIndex: "tongTien",
      key: "tongTien",
      render: (value) =>
        Number(value).toLocaleString("vi-VN", {
          style: "currency",
          currency: "VND",
        }),
    },
    {
      title: "Số sản phẩm",
      dataIndex: "chiTietHoaDons",
      key: "chiTietHoaDons",
      render: (items) => items?.length || 0,
    },
    {
      title: "Trạng thái",
      dataIndex: "trangThai",
      key: "trangThai",
      render: (value, record) => (
        <Select
          value={value || "PENDING"}
          options={statusOptions}
          onChange={(nextStatus) => handleStatusChange(record.id, nextStatus)}
          style={{ width: 150 }}
        />
      ),
    },
    {
      title: "Thao tác",
      key: "action",
      render: (_, record) => (
        <Button size="small" onClick={() => handleOpenOrderDetail(record)}>
          Chi tiết
        </Button>
      ),
    },
  ];

  return (
    <>
      <Card title="Danh sách đơn hàng" loading={loading}>
        <Table
          columns={columns}
          dataSource={orders}
          rowKey="id"
          pagination={{ pageSize: 8 }}
          expandable={{
            expandedRowRender: (record) => (
              <div className="space-y-2 bg-gray-50 p-4 rounded">
                {record.chiTietHoaDons?.length ? (
                  record.chiTietHoaDons.map((item) => (
                    <div
                      key={item.id}
                      className="flex items-center justify-between border-b border-gray-200 pb-2 last:border-none last:pb-0"
                    >
                      <Space>
                        <span className="font-medium">
                          {item.tenSach || item.sach?.tenSach}
                        </span>
                        <Tag color="purple">SL: {item.soLuong}</Tag>
                      </Space>
                      <span className="font-semibold text-[#c18653]">
                        {Number(
                          item.thanhTien || item.donGia * item.soLuong,
                        ).toLocaleString("vi-VN", {
                          style: "currency",
                          currency: "VND",
                        })}
                      </span>
                    </div>
                  ))
                ) : (
                  <p className="text-gray-500">Không có sản phẩm</p>
                )}
              </div>
            ),
          }}
        />
      </Card>

      <Modal
        title={`Chi tiết đơn hàng ${selectedOrder?.maHoaDon || ""}`}
        open={detailModalOpen}
        onCancel={() => {
          setDetailModalOpen(false);
          setSelectedOrder(null);
        }}
        footer={null}
        width={900}
      >
        {selectedOrder && (
          <div className="space-y-5">
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div className="rounded bg-gray-50 p-3">
                <div className="text-gray-500 mb-1">Mã hóa đơn</div>
                <div className="font-semibold">{selectedOrder.maHoaDon}</div>
              </div>
              <div className="rounded bg-gray-50 p-3">
                <div className="text-gray-500 mb-1">Khách hàng</div>
                <div className="font-semibold">
                  {selectedOrder.khachHang?.hoTen || "Khách vãng lai"}
                </div>
              </div>
              <div className="rounded bg-gray-50 p-3">
                <div className="text-gray-500 mb-1">Ngày bán</div>
                <div className="font-semibold">
                  {new Date(selectedOrder.ngayBan).toLocaleString("vi-VN")}
                </div>
              </div>
              <div className="rounded bg-gray-50 p-3">
                <div className="text-gray-500 mb-1">Tổng tiền</div>
                <div className="font-semibold text-[#c18653]">
                  {Number(selectedOrder.tongTien || 0).toLocaleString("vi-VN", {
                    style: "currency",
                    currency: "VND",
                  })}
                </div>
              </div>
            </div>

            <div className="flex items-center justify-between rounded border border-dashed border-gray-300 p-3">
              <span className="text-gray-500">Trạng thái</span>
              <Tag color={statusColor[selectedOrder.trangThai] || "default"}>
                {statusOptions.find(
                  (option) => option.value === selectedOrder.trangThai,
                )?.label ||
                  selectedOrder.trangThai ||
                  "Chưa xác định"}
              </Tag>
            </div>

            <div>
              <h4 className="font-semibold mb-3">Chi tiết sản phẩm</h4>
              {selectedOrder.chiTietHoaDons?.length ? (
                <Table
                  dataSource={selectedOrder.chiTietHoaDons}
                  rowKey="id"
                  pagination={false}
                  columns={[
                    {
                      title: "Sản phẩm",
                      dataIndex: "tenSach",
                      key: "tenSach",
                      render: (_, item) =>
                        item.tenSach || item.sach?.tenSach || "Sản phẩm",
                    },
                    {
                      title: "Số lượng",
                      dataIndex: "soLuong",
                      key: "soLuong",
                    },
                    {
                      title: "Đơn giá",
                      dataIndex: "donGia",
                      key: "donGia",
                      render: (value) =>
                        Number(value || 0).toLocaleString("vi-VN", {
                          style: "currency",
                          currency: "VND",
                        }),
                    },
                    {
                      title: "Thành tiền",
                      dataIndex: "thanhTien",
                      key: "thanhTien",
                      render: (value, item) =>
                        Number(
                          value || item.donGia * item.soLuong || 0,
                        ).toLocaleString("vi-VN", {
                          style: "currency",
                          currency: "VND",
                        }),
                    },
                  ]}
                />
              ) : (
                <p className="text-gray-500">
                  Không có sản phẩm nào trong đơn hàng
                </p>
              )}
            </div>
          </div>
        )}
      </Modal>
    </>
  );
}

export default OrderPage;
