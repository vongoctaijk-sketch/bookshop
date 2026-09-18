import React, { useEffect, useMemo, useState } from "react";
import {
  Button,
  Card,
  Divider,
  Form,
  Input,
  Modal,
  Radio,
  message,
  Typography,
} from "antd";
import { CreditCard, MapPin, ShoppingBag, Truck } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useAuthState } from "../../hooks/useAuth";
import hoaDonService from "../../services/Hoadonservice";
import { useCart } from "./CartContext";

const { Title, Text } = Typography;

function CheckoutPage() {
  const navigate = useNavigate();
  const { cartItems, clearCart } = useCart();
  const { user, isAuthenticated } = useAuthState();
  const [paymentMethod, setPaymentMethod] = useState("cod");
  const [form] = Form.useForm();
  const [confirmModalOpen, setConfirmModalOpen] = useState(false);
  const [pendingOrder, setPendingOrder] = useState(null);

  const totals = useMemo(() => {
    const subtotal = cartItems.reduce(
      (sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 1),
      0,
    );

    const shipping = subtotal > 0 ? 30000 : 0;
    const total = subtotal + shipping;

    return { subtotal, shipping, total };
  }, [cartItems]);

  useEffect(() => {
    if (user?.username && !form.getFieldValue("fullName")) {
      form.setFieldValue("fullName", user.username);
    }
  }, [user, form]);

  const handlePlaceOrder = async (values) => {
    if (!cartItems.length) {
      message.warning("Giỏ hàng hiện đang trống.");
      return;
    }

    const payload = {
      nguoiDungId: user.id,
      tenNguoiNhan: values?.fullName,
      sdtNguoiNhan: values?.phone,
      diaChiGiaoHang: values?.address,
      danhSachChiTiet: cartItems.map((item) => ({
        sachId: item.id,
        soLuong: item.quantity,
        donGia: Number(item.price || 0),
      })),
    };

    try {
      await hoaDonService.create(payload);

      message.success("Đặt hàng thành công! Cảm ơn bạn đã mua sắm.");
      setConfirmModalOpen(false);
      setPendingOrder(null);
      form.resetFields();
      clearCart();
      navigate("/");
    } catch (error) {
      const errorMessage =
        error?.response?.data?.message ||
        error?.response?.data?.error ||
        error?.message ||
        "Đặt hàng thất bại.";

      message.error(errorMessage);
    }
  };

  const formatMoney = (value) =>
    Number(value || 0).toLocaleString("vi-VN", {
      style: "currency",
      currency: "VND",
    });

  if (!cartItems.length) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center px-6 py-16">
        <Card className="max-w-xl w-full text-center">
          <ShoppingBag className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <Title level={3}>Giỏ hàng của bạn đang trống</Title>
          <Text type="secondary">
            Hãy thêm sản phẩm trước khi tiến hành thanh toán.
          </Text>
          <div className="mt-6">
            <Button type="primary" size="large" onClick={() => navigate("/")}>
              Tiếp tục mua sắm
            </Button>
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#f5f7f4] px-6 py-10">
      <div className="max-w-6xl mx-auto">
        <div className="mb-6">
          <Title level={2} className="!mb-2">
            Thanh toán
          </Title>
          <Text type="secondary">
            Hoàn tất thông tin giao hàng và chọn phương thức thanh toán.
          </Text>
        </div>

        <div className="grid grid-cols-1 xl:grid-cols-[1.3fr_0.7fr] gap-6">
          <div className="space-y-6">
            <Card title="Thông tin giao hàng" className="shadow-sm">
              {isAuthenticated && (
                <div className="mb-4 flex items-start gap-3 rounded-xl border border-[#e8d6ba] bg-[#f8f0e5] p-3 text-sm text-[#5f3f1d]">
                  <MapPin className="mt-0.5 h-4 w-4 text-[#c18653]" />
                  <span>
                    Tên người dùng đã được tự động điền. Vì hệ thống hiện chỉ
                    lưu tên đăng nhập, bạn vẫn cần nhập số điện thoại và địa chỉ
                    nhận hàng để hoàn tất đơn.
                  </span>
                </div>
              )}

              <Form
                form={form}
                layout="vertical"
                initialValues={{
                  fullName: user?.username || "",
                  phone: "",
                  address: "",
                }}
                onFinish={(values) => {
                  setPendingOrder(values);
                  setConfirmModalOpen(true);
                }}
              >
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <Form.Item
                    label="Họ và tên"
                    name="fullName"
                    rules={[
                      { required: true, message: "Vui lòng nhập họ tên" },
                    ]}
                  >
                    <Input size="large" placeholder="Nhập họ tên" />
                  </Form.Item>

                  <Form.Item
                    label="Số điện thoại"
                    name="phone"
                    rules={[
                      {
                        required: true,
                        message: "Vui lòng nhập số điện thoại",
                      },
                    ]}
                  >
                    <Input size="large" placeholder="Nhập số điện thoại" />
                  </Form.Item>
                </div>

                <Form.Item
                  label="Địa chỉ nhận hàng"
                  name="address"
                  rules={[{ required: true, message: "Vui lòng nhập địa chỉ" }]}
                >
                  <Input.TextArea
                    rows={4}
                    placeholder="Ví dụ: 123 Lê Lợi, Quận 1, TP.HCM"
                  />
                </Form.Item>
              </Form>
            </Card>

            <Card title="Phương thức thanh toán" className="shadow-sm">
              <Radio.Group
                onChange={(e) => setPaymentMethod(e.target.value)}
                value={paymentMethod}
                className="w-full"
              >
                <div className="space-y-3">
                  <Radio value="cod" className="!block !w-full">
                    <div className="flex items-center gap-3 rounded-lg border border-gray-200 p-3 w-full">
                      <Truck className="w-5 h-5 text-[#c18653]" />
                      <div>
                        <div className="font-semibold text-gray-800">
                          Thanh toán khi nhận hàng
                        </div>
                        <div className="text-sm text-gray-500">
                          COD - thanh toán trực tiếp khi giao hàng
                        </div>
                      </div>
                    </div>
                  </Radio>

                  <Radio value="bank" className="!block !w-full">
                    <div className="flex items-center gap-3 rounded-lg border border-gray-200 p-3 w-full">
                      <CreditCard className="w-5 h-5 text-[#c18653]" />
                      <div>
                        <div className="font-semibold text-gray-800">
                          Chuyển khoản ngân hàng
                        </div>
                        <div className="text-sm text-gray-500">
                          Thanh toán trước bằng ngân hàng
                        </div>
                      </div>
                    </div>
                  </Radio>
                </div>
              </Radio.Group>
            </Card>
          </div>

          <div>
            <Card title="Tóm tắt đơn hàng" className="shadow-sm sticky top-6">
              <div className="space-y-4">
                {cartItems.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center gap-3 border-b border-gray-200 pb-3 last:border-none last:pb-0"
                  >
                    <div className="w-16 h-20 rounded overflow-hidden bg-gray-100 border border-gray-200 flex-shrink-0">
                      <img
                        src={item.image || item.hinhAnh}
                        alt={item.title || item.tenSach}
                        className="w-full h-full object-cover"
                      />
                    </div>

                    <div className="flex-1 min-w-0">
                      <div className="font-semibold text-gray-800 line-clamp-2">
                        {item.title || item.tenSach}
                      </div>
                      <div className="text-sm text-gray-500 mt-1">
                        Số lượng: {item.quantity}
                      </div>
                    </div>

                    <div className="font-semibold text-[#c18653]">
                      {formatMoney((item.price || 0) * (item.quantity || 1))}
                    </div>
                  </div>
                ))}
              </div>

              <Divider />

              <div className="space-y-2 text-sm">
                <div className="flex justify-between text-gray-600">
                  <span>Tạm tính</span>
                  <span>{formatMoney(totals.subtotal)}</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Phí vận chuyển</span>
                  <span>{formatMoney(totals.shipping)}</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Giảm giá</span>
                  <span>0₫</span>
                </div>
              </div>

              <Divider />

              <div className="flex justify-between items-center mb-4">
                <span className="text-lg font-semibold text-gray-800">
                  Tổng cộng
                </span>
                <span className="text-2xl font-bold text-[#c18653]">
                  {formatMoney(totals.total)}
                </span>
              </div>

              <Button
                type="primary"
                size="large"
                block
                onClick={() => form.submit()}
                className="!bg-[#c18653] !border-[#c18653] hover:!bg-[#a67144]"
              >
                Xác nhận đặt hàng
              </Button>
            </Card>
          </div>
        </div>
      </div>

      <Modal
        title="Xác nhận đặt hàng"
        open={confirmModalOpen}
        onCancel={() => setConfirmModalOpen(false)}
        footer={[
          <Button key="cancel" onClick={() => setConfirmModalOpen(false)}>
            Hủy
          </Button>,
          <Button
            key="confirm"
            type="primary"
            className="!bg-[#18352a] !border-[#18352a] hover:!bg-[#254433]"
            onClick={() => handlePlaceOrder(pendingOrder)}
          >
            Xác nhận
          </Button>,
        ]}
      >
        <div className="space-y-3">
          <p className="text-base font-semibold text-gray-800">
            Bạn chắc chắn muốn đặt hàng?
          </p>
          <p className="text-sm text-gray-600">
            Hóa đơn sẽ được tạo với thông tin giao hàng đã nhập, và bạn sẽ được
            chuyển về trang chủ sau khi xác nhận.
          </p>
          <div className="rounded-xl bg-gray-50 p-3 text-sm text-gray-700">
            <div>Người nhận: {pendingOrder?.fullName || "-"}</div>
            <div>SĐT: {pendingOrder?.phone || "-"}</div>
            <div>Tổng tiền: {formatMoney(totals.total)}</div>
          </div>
        </div>
      </Modal>
    </div>
  );
}

export default CheckoutPage;
