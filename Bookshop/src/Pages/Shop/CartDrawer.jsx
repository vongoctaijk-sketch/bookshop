import { Drawer } from "antd";
import { Trash2, Minus, Plus, ShoppingCart } from "lucide-react";
import { useCart } from "./CartContext";

function CartDrawer() {
  const { cartItems, isCartOpen, closeCart, removeFromCart, updateQuantity } =
    useCart();

  const totalPrice = cartItems.reduce(
    (sum, item) => sum + (item.price || 0) * item.quantity,
    0,
  );

  const handleQuantityChange = (id, newQuantity) => {
    if (newQuantity > 0) {
      updateQuantity(id, newQuantity);
    }
  };

  return (
    <Drawer
      title={
        <div className="flex items-center gap-2 text-[#18352a]">
          <ShoppingCart className="w-5 h-5" />
          <span>Giỏ Hàng</span>
        </div>
      }
      placement="right"
      onClose={closeCart}
      open={isCartOpen}
      width={420}
      bodyStyle={{ padding: 0 }}
      headerStyle={{ borderBottom: "1px solid #e5e7eb" }}
    >
      {cartItems.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-16 px-6">
          <ShoppingCart className="w-16 h-16 text-gray-300 mb-4" />
          <p className="text-gray-500 text-lg font-medium">Giỏ hàng trống</p>
          <p className="text-gray-400 text-sm mt-2">
            Hãy thêm một số sản phẩm để bắt đầu mua sắm
          </p>
          <button
            onClick={closeCart}
            className="mt-6 px-6 py-2 bg-[#18352a] text-white rounded-lg hover:bg-[#254433] transition-colors"
          >
            Tiếp Tục Mua Sắm
          </button>
        </div>
      ) : (
        <div className="flex flex-col h-full">
          {/* Cart Items Section */}
          <div className="flex-1 overflow-y-auto px-6 py-4">
            <div className="space-y-4">
              {cartItems.map((item) => (
                <div
                  key={item.id}
                  className="flex gap-4 p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  {/* Item Image */}
                  {item.image && (
                    <div className="w-20 h-24 flex-shrink-0 bg-white rounded overflow-hidden border border-gray-200">
                      <img
                        src={item.image}
                        alt={item.title}
                        className="w-full h-full object-cover"
                      />
                    </div>
                  )}

                  {/* Item Details */}
                  <div className="flex-1 min-w-0">
                    <h4 className="font-semibold text-gray-800 text-sm line-clamp-2 mb-2">
                      {item.title}
                    </h4>

                    {/* Price */}
                    <div className="mb-3">
                      <p className="text-[#c18653] font-bold text-base mb-1">
                        ₫
                        {((item.price || 0) * item.quantity).toLocaleString(
                          "vi-VN",
                        )}
                      </p>
                      <p className="text-gray-500 text-xs">
                        Đơn giá: ₫{(item.price || 0).toLocaleString("vi-VN")}
                      </p>
                    </div>

                    {/* Quantity Control */}
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2 bg-white rounded-lg border border-gray-300">
                        <button
                          onClick={() =>
                            handleQuantityChange(item.id, item.quantity - 1)
                          }
                          className="p-1 hover:bg-gray-100 transition-colors"
                          title="Giảm số lượng"
                        >
                          <Minus className="w-4 h-4 text-gray-600" />
                        </button>
                        <span className="px-3 py-1 font-semibold text-gray-800 text-sm min-w-8 text-center">
                          {item.quantity}
                        </span>
                        <button
                          onClick={() =>
                            handleQuantityChange(item.id, item.quantity + 1)
                          }
                          className="p-1 hover:bg-gray-100 transition-colors"
                          title="Tăng số lượng"
                        >
                          <Plus className="w-4 h-4 text-gray-600" />
                        </button>
                      </div>
                      <button
                        onClick={() => removeFromCart(item.id)}
                        className="p-2 text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                        title="Xóa khỏi giỏ"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Cart Footer */}
          <div className="border-t border-gray-200 p-6 bg-gray-50">
            {/* Total */}
            <div className="flex justify-between items-center mb-6 pb-4 border-b border-gray-200">
              <span className="text-gray-700 font-medium">Tổng cộng:</span>
              <span className="text-2xl font-bold text-[#c18653]">
                ₫{totalPrice.toLocaleString("vi-VN")}
              </span>
            </div>

            {/* Buttons */}
            <div className="space-y-3">
              <button
                onClick={closeCart}
                className="w-full px-4 py-3 bg-[#c18653] text-white font-semibold rounded-lg hover:bg-[#a67144] transition-colors duration-200 shadow-md hover:shadow-lg"
              >
                Thanh Toán
              </button>
              <button
                onClick={closeCart}
                className="w-full px-4 py-3 bg-white text-[#18352a] font-semibold rounded-lg border-2 border-[#18352a] hover:bg-[#18352a] hover:text-white transition-colors duration-200"
              >
                Tiếp Tục Mua Sắm
              </button>
            </div>
          </div>
        </div>
      )}
    </Drawer>
  );
}

export default CartDrawer;
