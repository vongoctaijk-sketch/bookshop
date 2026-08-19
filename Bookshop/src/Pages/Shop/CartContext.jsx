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
    setCartItems((prevItems) => {
      const existingItem = prevItems.find((item) => item.id === book.id);

      // Nếu sách đã có → tăng quantity
      if (existingItem) {
        return prevItems.map((item) =>
          item.id === book.id
            ? {
                ...item,
                quantity: item.quantity + 1,
              }
            : item,
        );
      }

      // Nếu chưa có → thêm mới
      return [
        ...prevItems,
        {
          ...book,
          quantity: 1,
        },
      ];
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

  return (
    <CartContext.Provider
      value={{
        cartItems,
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
