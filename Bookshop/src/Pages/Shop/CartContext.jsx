import { createContext, useContext, useEffect, useState } from "react";

const CartContext = createContext();

export function CartProvider({ children }) {
  // Lấy cart từ localStorage khi mở trang
  const [cartItems, setCartItems] = useState(() => {
    const savedCart = localStorage.getItem("cart");

    return savedCart ? JSON.parse(savedCart) : [];
  });

  const [isCartOpen, setIsCartOpen] = useState(false);

  // Mỗi khi cart thay đổi → lưu lại localStorage
  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cartItems));
  }, [cartItems]);

  // Mở Cart
  const openCart = () => {
    setIsCartOpen(true);
  };

  // Đóng Cart
  const closeCart = () => {
    setIsCartOpen(false);
  };

  // Thêm sản phẩm
  const addToCart = (book) => {
    const normalizedBook = {
      ...book,
      id: book.id,
      title: book.tenSach || book.title,
      author:
        book.tacGias?.map((t) => t.hoTen || t.tenTacGia || t.name).join(", ") ||
        book.author ||
        "Chưa cập nhật",
      image: book.hinhAnh || book.image,
      price: Number(book.giaBan ?? book.price ?? 0),
      publisher: book.nhaXuatBan?.tenNxb || book.publisher || "Chưa cập nhật",
      year: book.namXuatBan ?? book.year ?? "Chưa cập nhật",
      pages: book.soTrang ?? book.pages ?? "Chưa cập nhật",
      quantity: 1,
    };

    setCartItems((prevItems) => {
      const existingItem = prevItems.find(
        (item) => item.id === normalizedBook.id,
      );

      if (existingItem) {
        return prevItems.map((item) =>
          item.id === normalizedBook.id
            ? {
                ...item,
                quantity: item.quantity + 1,
              }
            : item,
        );
      }

      return [...prevItems, normalizedBook];
    });
  };

  // Xóa sản phẩm
  const removeFromCart = (bookId) => {
    setCartItems((prevItems) => prevItems.filter((item) => item.id !== bookId));
  };

  // Tăng / giảm số lượng
  const updateQuantity = (bookId, quantity) => {
    setCartItems((prevItems) =>
      prevItems.map((item) =>
        item.id === bookId
          ? {
              ...item,
              quantity,
            }
          : item,
      ),
    );
  };

  // Xóa toàn bộ Cart
  const clearCart = () => {
    setCartItems([]);
  };

  const cartItemCount = cartItems.reduce(
    (total, item) => total + (item.quantity || 1),
    0,
  );

  return (
    <CartContext.Provider
      value={{
        cartItems,
        cartItemCount,
        isCartOpen,
        openCart,
        closeCart,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
      }}
    >
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}
