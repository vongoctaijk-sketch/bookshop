import { useLocation, Navigate } from "react-router-dom";
import { useCart } from "./CartContext";
import {
  ShoppingCart,
  Heart,
  Share2,
  Star,
  Truck,
  RotateCcw,
  Shield,
} from "lucide-react";
import { useState } from "react";

const BookDetailsPage = () => {
  const location = useLocation();
  const { addToCart } = useCart();
  const [quantity, setQuantity] = useState(1);
  const [isFavorite, setIsFavorite] = useState(false);

  // Lấy dữ liệu bookData từ state
  const book = location.state?.bookData;

  // Xử lý lỗi khi F5 (mất state): Đẩy về trang chủ
  if (!book) {
    return <Navigate to="/" replace />;
  }

  const handleAddToCart = () => {
    for (let i = 0; i < quantity; i++) {
      addToCart(book);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-white to-gray-50">
      {/* Breadcrumb */}
      <div className="bg-white border-b border-gray-200">
        <div className="max-w-6xl mx-auto px-6 py-4">
          <nav className="text-sm text-gray-600">
            <span>
              <a href="/" className="text-[#c18653] hover:underline">
                Trang chủ
              </a>
            </span>
            <span className="mx-2">/</span>
            <span>
              <a href="/shop" className="text-[#c18653] hover:underline">
                Cửa hàng
              </a>
            </span>
            <span className="mx-2">/</span>
            <span className="text-gray-800 font-medium">{book.title}</span>
          </nav>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-6xl mx-auto px-6 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
          {/* Left Column - Image */}
          <div className="flex flex-col">
            <div className="bg-white rounded-lg shadow-lg overflow-hidden border border-gray-200 mb-6">
              <div className="aspect-[3/4] bg-gray-100 flex items-center justify-center">
                <img
                  src={book.image}
                  alt={book.title}
                  className="w-full h-full object-cover hover:scale-105 transition-transform duration-300"
                />
              </div>
            </div>

            {/* Thumbnail Section */}
            <div className="flex gap-3">
              <div className="w-16 h-20 bg-white rounded border-2 border-[#c18653] cursor-pointer overflow-hidden">
                <img
                  src={book.image}
                  alt="Thumbnail"
                  className="w-full h-full object-cover"
                />
              </div>
              <div className="w-16 h-20 bg-white rounded border border-gray-300 cursor-pointer overflow-hidden hover:border-[#c18653] transition-colors">
                <img
                  src={book.image}
                  alt="Thumbnail"
                  className="w-full h-full object-cover opacity-70"
                />
              </div>
            </div>
          </div>

          {/* Right Column - Details */}
          <div className="flex flex-col">
            {/* Rating */}
            <div className="flex items-center gap-2 mb-4">
              <div className="flex gap-1">
                {[...Array(5)].map((_, i) => (
                  <Star
                    key={i}
                    className={`w-5 h-5 ${
                      i < 4 ? "fill-[#d4995f] text-[#d4995f]" : "text-gray-300"
                    }`}
                  />
                ))}
              </div>
              <span className="text-gray-600 text-sm">(124 bình luận)</span>
            </div>

            {/* Title */}
            <h1 className="text-4xl font-bold text-gray-900 mb-4 leading-tight">
              {book.title}
            </h1>

            {/* Price Section */}
            <div className="bg-gradient-to-r from-[#d4995f] to-[#c18653] rounded-lg p-6 mb-6 text-white">
              <p className="text-sm font-semibold uppercase tracking-wide mb-2">
                Giá bán
              </p>
              <div className="flex items-baseline gap-4">
                <p className="text-4xl font-bold">
                  ₫{book.price?.toLocaleString("vi-VN") || 0}
                </p>
                <p className="text-lg line-through opacity-80">
                  ₫
                  {(book.price
                    ? Math.round(book.price * 1.2)
                    : 0
                  ).toLocaleString("vi-VN")}
                </p>
              </div>
            </div>

            {/* Book Info */}
            <div className="bg-gray-50 rounded-lg p-6 mb-6 space-y-3 text-sm">
              <div className="flex justify-between border-b border-gray-200 pb-3">
                <span className="text-gray-600 font-medium">Tác giả:</span>
                <span className="text-gray-900 font-semibold">
                  {book.author || "Chưa cập nhật"}
                </span>
              </div>
              <div className="flex justify-between border-b border-gray-200 pb-3">
                <span className="text-gray-600 font-medium">Nhà xuất bản:</span>
                <span className="text-gray-900 font-semibold">
                  {book.publisher || "Chưa cập nhật"}
                </span>
              </div>
              <div className="flex justify-between border-b border-gray-200 pb-3">
                <span className="text-gray-600 font-medium">Năm xuất bản:</span>
                <span className="text-gray-900 font-semibold">
                  {book.year || "Chưa cập nhật"}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600 font-medium">Số trang:</span>
                <span className="text-gray-900 font-semibold">
                  {book.pages || "Chưa cập nhật"}
                </span>
              </div>
            </div>

            {/* Quantity and Buttons */}
            <div className="space-y-4 mb-8">
              <div className="flex items-center gap-4">
                <span className="text-gray-700 font-medium">Số lượng:</span>
                <div className="flex items-center gap-2 bg-gray-100 rounded-lg border border-gray-300">
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    className="px-4 py-3 hover:bg-gray-200 transition-colors text-gray-600 font-semibold"
                  >
                    −
                  </button>
                  <input
                    type="number"
                    value={quantity}
                    onChange={(e) =>
                      setQuantity(Math.max(1, parseInt(e.target.value) || 1))
                    }
                    className="w-16 text-center border-none bg-transparent font-semibold text-gray-900 focus:outline-none"
                  />
                  <button
                    onClick={() => setQuantity(quantity + 1)}
                    className="px-4 py-3 hover:bg-gray-200 transition-colors text-gray-600 font-semibold"
                  >
                    +
                  </button>
                </div>
              </div>

              {/* Action Buttons */}
              <button
                onClick={handleAddToCart}
                className="w-full bg-[#c18653] hover:bg-[#a67144] text-white font-bold py-4 px-6 rounded-lg flex items-center justify-center gap-2 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:scale-105"
              >
                <ShoppingCart className="w-5 h-5" />
                <span>Thêm Vào Giỏ Hàng</span>
              </button>
            </div>

            {/* Secondary Actions */}
            <div className="flex gap-3 mb-8">
              <button
                onClick={() => setIsFavorite(!isFavorite)}
                className={`flex-1 py-3 px-4 rounded-lg font-semibold flex items-center justify-center gap-2 transition-colors border-2 ${
                  isFavorite
                    ? "bg-red-50 border-red-300 text-red-600"
                    : "bg-white border-gray-300 text-gray-600 hover:border-red-300"
                }`}
              >
                <Heart
                  className={`w-5 h-5 ${isFavorite ? "fill-red-600" : ""}`}
                />
                <span>{isFavorite ? "Đã thích" : "Thích"}</span>
              </button>
              <button className="flex-1 py-3 px-4 border-2 border-gray-300 text-gray-600 font-semibold rounded-lg hover:border-[#c18653] hover:text-[#c18653] transition-colors flex items-center justify-center gap-2">
                <Share2 className="w-5 h-5" />
                <span>Chia sẻ</span>
              </button>
            </div>

            {/* Benefits */}
            <div className="bg-white rounded-lg border border-gray-200 p-6 space-y-4">
              <div className="flex gap-4">
                <Truck className="w-6 h-6 text-[#c18653] flex-shrink-0" />
                <div>
                  <p className="font-semibold text-gray-900">
                    Miễn phí vận chuyển
                  </p>
                  <p className="text-sm text-gray-600">
                    Cho đơn hàng từ 50.000₫
                  </p>
                </div>
              </div>
              <div className="flex gap-4">
                <RotateCcw className="w-6 h-6 text-[#c18653] flex-shrink-0" />
                <div>
                  <p className="font-semibold text-gray-900">
                    Hoàn tiền 30 ngày
                  </p>
                  <p className="text-sm text-gray-600">
                    Nếu không hài lòng với sản phẩm
                  </p>
                </div>
              </div>
              <div className="flex gap-4">
                <Shield className="w-6 h-6 text-[#c18653] flex-shrink-0" />
                <div>
                  <p className="font-semibold text-gray-900">
                    Thanh toán an toàn
                  </p>
                  <p className="text-sm text-gray-600">Mã hóa SSL 256-bit</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookDetailsPage;
