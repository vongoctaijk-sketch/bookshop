import React from "react";
import { useState } from "react";
import { Pagination } from "antd";
import { Button } from "antd";
import { FilterOutlined, StarFilled } from "@ant-design/icons";
import { useCart } from "./CartContext";
import { useNavigate } from "react-router-dom";
import CartDrawer from "./CartDrawer";
import sachService from "../../services/Sachservice";
import theloaiService from "../../services/Theloaiservice";
import { useSearchParams } from "react-router-dom";

// Mock data dựa trên hình ảnh

const books = [
  {
    id: 1,
    title: "The Midnight Library",
    author: "Matt Haig",
    rating: 4.8,
    price: "£18.99",
    image:
      "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
  {
    id: 2,
    title: "Sapiens: A Brief History",
    author: "Yuval Noah Harari",
    rating: 4.7,
    price: "£22.50",
    image:
      "https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
  {
    id: 3,
    title: "The Name of the Rose",
    author: "Umberto Eco",
    rating: 4.6,
    price: "£16.75",
    image:
      "https://images.unsplash.com/photo-1589998059171-989d887dda6e?auto=format&fit=crop&q=80&w=400",
    lowStock: true,
  },
  {
    id: 4,
    title: "Dune",
    author: "Frank Herbert",
    rating: 4.9,
    price: "£19.99",
    image:
      "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
  {
    id: 5,
    title: "Educated",
    author: "Tara Westover",
    rating: 4.8,
    price: "£17.99",
    image:
      "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
  {
    id: 6,
    title: "The Silk Roads",
    author: "Peter Frankopan",
    rating: 4.5,
    price: "£21.00",
    image:
      "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
  {
    id: 7,
    title: "The Silk Roads",
    author: "Peter Frankopan",
    rating: 4.5,
    price: "£21.00",
    image:
      "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
  {
    id: 8,
    title: "The Silk Roads",
    author: "Peter Frankopan",
    rating: 4.5,
    price: "£21.00",
    image:
      "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
  {
    id: 9,
    title: "The Silk Roads",
    author: "Peter Frankopan",
    rating: 4.5,
    price: "£21.00",
    image:
      "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
  {
    id: 10,
    title: "The Silk Roads",
    author: "Peter Frankopan",
    rating: 4.5,
    price: "£21.00",
    image:
      "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
  {
    id: 11,
    title: "The Silk Roads",
    author: "Peter Frankopan",
    rating: 4.5,
    price: "£21.00",
    image:
      "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
];
const books2 = [
  {
    id: 1,
    title: "The Midnight Library",
    author: "Matt Haig",
    rating: 4.8,
    price: "£18.99",
    image:
      "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
  {
    id: 2,
    title: "Sapiens: A Brief History",
    author: "Yuval Noah Harari",
    rating: 4.7,
    price: "£22.50",
    image:
      "https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
  {
    id: 3,
    title: "The Name of the Rose",
    author: "Umberto Eco",
    rating: 4.6,
    price: "£16.75",
    image:
      "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400",
    lowStock: false,
  },
];

function ShopPage() {
  const [currentPage, setCurrentPage] = useState(1);
  const { addToCart } = useCart();
  const pageSize = 6;
  const [theloai, setTheloai] = useState({ id: null, tenTheLoai: "All" });
  const [bookdata, setBookdata] = useState([]);
  const [theloaidata, setTheloaidata] = useState([]);
  const categories = [{ id: null, tenTheLoai: "All" }, ...theloaidata];
  const [searchParams] = useSearchParams();
  const keyword = searchParams.get("keyword") || "";
  const startIndex = (currentPage - 1) * pageSize;
  const currentBooks = books.slice(startIndex, startIndex + pageSize);
  const navigate = useNavigate();
  const [totalBooks, setTotalBooks] = useState(0);
  React.useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await theloaiService.getAll();
        setTheloaidata(response.data);
      } catch (error) {
        console.error("Error fetching categories:", error);
      }
    };

    fetchCategories();
  }, []);
  React.useEffect(() => {
    const fetchBooks = async () => {
      try {
        const response = await sachService.getAll({
          keyword,
          theLoaiId: theloai.id,
          page: currentPage - 1,
          size: pageSize,
        });

        setBookdata(response.data.content);
        setTotalBooks(response.data.totalElements);
      } catch (error) {
        console.error("Error fetching books:", error);
      }
    };

    fetchBooks();
  }, [keyword, theloai.id, currentPage]);
  const handleBookClick = (book) => {
    // Điều hướng sang trang chi tiết và kẹp toàn bộ object book vào state
    navigate(`/books/${book.id}`, { state: { bookData: book } });
  };

  return (
    <>
      <div className="w-full font-sans">
        {/* SECTION 1: HERO */}
        <div className="flex w-full px-16 py-16 bg-[#1b3627]">
          {/* Content 1 - bên trái */}
          <div className="w-1/2 pr-12 flex flex-col justify-center">
            <h2 className="text-sm tracking-widest font-semibold text-[#c87a50] uppercase mb-4">
              Featured Collection
            </h2>

            <h1 className="text-5xl font-bold text-white leading-tight font-serif">
              Every Story <br />
              <span className="italic font-normal">Deserves a</span> <br />
              Good Reader
            </h1>

            <p className="text-[15px] text-gray-300 mt-6 max-w-md leading-relaxed">
              Curated editions for curious minds. From timeless classics to
              contemporary masterworks — discover your next great read.
            </p>

            <div className="flex gap-4 items-center mt-8">
              <Button className="!bg-[#c87a50] hover:!bg-[#a7633d] !text-white px-6 py-5 rounded-sm border-none font-semibold">
                Browse Collection
              </Button>

              <Button
                ghost
                className="hover:!text-white hover:!border-white !text-gray-300 !border-gray-400 px-6 py-5 rounded-sm font-semibold"
              >
                Fiction Picks
              </Button>
            </div>
          </div>

          {/* Content 2 - bên phải (Images with staggered layout) */}
          <div className="w-1/2 flex gap-4 h-[350px] items-center justify-end ">
            {books2.map((book, index) => (
              <img
                onClick={() => handleBookClick(book)}
                key={book.id}
                src={book.image}
                alt={book.title}
                className={`w-1/3 h-[90%] object-cover rounded-sm shadow-lg transition-transform duration-300 hover:scale-105
        ${index === 0 ? "translate-y-8" : ""} 
        ${index === 1 ? "-translate-y-6" : ""} 
        ${index === 2 ? "translate-y-2" : ""}
      `}
              />
            ))}
          </div>
        </div>

        {/* SECTION 2: FILTER & PRODUCT GRID */}
        <div className="bg-[#f8f6f0] min-h-screen">
          {/* Filter Bar */}
          <div className="flex items-center px-16 py-4 bg-[#f0ece1] border-b border-[#e5e0d3] gap-8">
            <FilterOutlined className="text-xl text-gray-500 cursor-pointer" />
            <div className="flex gap-2 flex-wrap">
              {categories.map((cat) => (
                <span
                  key={cat.id ?? "all"}
                  className={`px-4 py-1.5 cursor-pointer text-sm font-medium rounded-sm transition-colors ${
                    cat.id === theloai.id
                      ? "bg-[#1b3627] text-white"
                      : "text-gray-600 hover:bg-gray-200/50"
                  }`}
                  onClick={() => {
                    setTheloai(cat);
                    setCurrentPage(1);
                  }}
                >
                  {cat.tenTheLoai}
                </span>
              ))}
            </div>
          </div>

          {/* Product Grid */}
          <div className="px-16 py-10">
            <div className="mb-8">
              <h2 className="text-3xl font-serif text-gray-900">
                All Books{" "}
                <span className="text-base text-gray-500 font-sans font-normal ml-1">
                  (12)
                </span>
              </h2>
            </div>

            {/* Grid Layout - 6 columns */}
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
              {bookdata.map((book) => (
                <div key={book.id} className="flex flex-col group">
                  <div className="relative w-full aspect-[2/3] bg-gray-200 mb-4 overflow-hidden rounded-sm">
                    <img
                      onClick={() => handleBookClick(book)}
                      src={book.hinhAnh}
                      alt={book.tenSach}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                    />
                  </div>

                  <h3 className="font-serif text-lg font-semibold text-gray-900 leading-tight mb-1 truncate">
                    {book.tenSach}
                  </h3>
                  <p className="text-xs text-gray-600 mb-1">{book.tacGia}</p>

                  {/* <div className="flex items-center text-xs text-gray-700 font-medium mb-3">
                    <StarFilled className="text-[#c87a50] mr-1 text-[10px]" />
                    {book.rating}
                  </div> */}

                  <div className="flex items-center justify-between mt-auto">
                    <span className="text-sm font-semibold text-gray-900">
                      {book.giaBan.toLocaleString("vi-VN", {
                        style: "currency",
                        currency: "VND",
                      })}
                    </span>
                    <Button
                      className="!bg-[#1b3627] hover:!bg-[#12241a] !text-white border-none text-xs px-4 rounded-sm h-7"
                      onClick={() => addToCart(book)}
                    >
                      Add
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </div>
          <div className="flex justify-center mt-8">
            <Pagination
              current={currentPage}
              pageSize={pageSize}
              total={totalBooks}
              onChange={(page) => setCurrentPage(page)}
            />
          </div>
        </div>
      </div>
      <CartDrawer />
    </>
  );
}

export default ShopPage;
