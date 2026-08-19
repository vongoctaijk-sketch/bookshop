import React from "react";
import { Outlet } from "react-router-dom";
import { Layout } from "antd";
const { Header, Content, Footer } = Layout;
import { BookOpen, Search, ShoppingCart } from "lucide-react";
import { useCart } from "../../../Pages/Shop/CartContext";
import { useNavigate } from "react-router-dom";
function ShopLayout() {
  const navigate = useNavigate();
  const { openCart, cartItems } = useCart();
  const [keyword, setKeyword] = React.useState("");
  const handleSearch = () => {
    navigate(`/?keyword=${encodeURIComponent(keyword)}`);
  };
  return (
    <Layout className="!min-h-screen ">
      <Header className="!bg-[#18352a] !h-20 flex items-center justify-between !px-6 !py-12 sticky top-0 z-50">
        <div className="flex items-center gap-3 ">
          <BookOpen className="text-[#d4995f] w-7 h-7" strokeWidth={1.5} />
          <span className="font-serif text-white text-2xl font-semibold tracking-wide">
            Folio & Spine
          </span>
        </div>
        <div className="hidden md:flex flex-1 h-12 max-w-2xl mx-8 relative">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-5 w-5 text-gray-400" />
          </div>
          <input
            type="text"
            placeholder="Search books, authors..."
            className="w-full bg-[#254433] text-gray-200 placeholder-gray-400 rounded-md py-2.5 pl-11 pr-4 
                       focus:outline-none focus:ring-1 focus:ring-[#d4995f] transition-all border-none"
            onChange={(e) => setKeyword(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                handleSearch();
              }
            }}
          />
        </div>
        <div className="shrink-0">
          <button
            className="flex items-center gap-2 bg-[#c18653] hover:bg-[#a67144] text-white px-6 py-2.5 rounded-md transition-colors duration-200 border-none cursor-pointer"
            onClick={() => {
              openCart();
            }}
          >
            <ShoppingCart className="w-5 h-5" />
            <span className="font-medium text-base">Cart</span>
          </button>
        </div>
      </Header>
      <Content className="flex-1 ">
        <Outlet />
      </Content>
      <Footer className="!bg-[#18352a] !text-white !p-0">
        <div className="bg-[#18352a] text-white pt-12 pb-8 px-6">
          <div className="max-w-6xl mx-auto">
            {/* Footer Grid */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
              {/* Brand Section */}
              <div>
                <div className="flex items-center gap-2 mb-4">
                  <BookOpen
                    className="text-[#d4995f] w-6 h-6"
                    strokeWidth={1.5}
                  />
                  <span className="font-serif text-xl font-semibold tracking-wide">
                    Folio & Spine
                  </span>
                </div>
                <p className="text-gray-300 text-sm leading-relaxed">
                  Discover your next favorite book from our curated collection
                  of literary treasures.
                </p>
              </div>

              {/* Quick Links */}
              <div>
                <h4 className="font-semibold text-[#d4995f] mb-4">
                  Quick Links
                </h4>
                <ul className="space-y-2 text-sm text-gray-300">
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      Home
                    </a>
                  </li>
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      Shop
                    </a>
                  </li>
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      Categories
                    </a>
                  </li>
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      Best Sellers
                    </a>
                  </li>
                </ul>
              </div>

              {/* Customer Service */}
              <div>
                <h4 className="font-semibold text-[#d4995f] mb-4">
                  Customer Service
                </h4>
                <ul className="space-y-2 text-sm text-gray-300">
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      Contact Us
                    </a>
                  </li>
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      FAQs
                    </a>
                  </li>
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      Shipping Info
                    </a>
                  </li>
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      Returns
                    </a>
                  </li>
                </ul>
              </div>

              {/* Legal */}
              <div>
                <h4 className="font-semibold text-[#d4995f] mb-4">Legal</h4>
                <ul className="space-y-2 text-sm text-gray-300">
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      Privacy Policy
                    </a>
                  </li>
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      Terms of Service
                    </a>
                  </li>
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      Cookie Policy
                    </a>
                  </li>
                  <li>
                    <a
                      href="#"
                      className="hover:text-[#d4995f] transition-colors"
                    >
                      Accessibility
                    </a>
                  </li>
                </ul>
              </div>
            </div>

            {/* Divider */}
            <div className="border-t border-gray-600 pt-6 mt-6">
              {/* Social & Newsletter */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                <div>
                  <h4 className="font-semibold text-[#d4995f] mb-3">
                    Follow Us
                  </h4>
                  <div className="flex gap-4">
                    <a
                      href="#"
                      className="text-gray-300 hover:text-[#d4995f] transition-colors text-sm"
                    >
                      Facebook
                    </a>
                    <a
                      href="#"
                      className="text-gray-300 hover:text-[#d4995f] transition-colors text-sm"
                    >
                      Twitter
                    </a>
                    <a
                      href="#"
                      className="text-gray-300 hover:text-[#d4995f] transition-colors text-sm"
                    >
                      Instagram
                    </a>
                    <a
                      href="#"
                      className="text-gray-300 hover:text-[#d4995f] transition-colors text-sm"
                    >
                      LinkedIn
                    </a>
                  </div>
                </div>
                <div>
                  <h4 className="font-semibold text-[#d4995f] mb-3">
                    Newsletter
                  </h4>
                  <div className="flex gap-2">
                    <input
                      type="email"
                      placeholder="Enter your email"
                      className="flex-1 px-3 py-2 bg-[#254433] text-white placeholder-gray-400 rounded text-sm focus:outline-none focus:ring-1 focus:ring-[#d4995f]"
                    />
                    <button className="px-4 py-2 bg-[#c18653] hover:bg-[#a67144] text-white rounded text-sm transition-colors">
                      Subscribe
                    </button>
                  </div>
                </div>
              </div>

              {/* Copyright */}
              <div className="border-t border-gray-600 pt-6 text-center text-sm text-gray-400">
                <p>&copy; 2024 Folio & Spine. All rights reserved.</p>
              </div>
            </div>
          </div>
        </div>
      </Footer>
    </Layout>
  );
}

export default ShopLayout;
