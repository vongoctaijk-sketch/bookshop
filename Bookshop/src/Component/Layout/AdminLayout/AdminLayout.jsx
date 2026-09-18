import React from "react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { Layout, Menu, Button, Typography } from "antd";
import {
  BookOpen,
  LayoutDashboard,
  ShoppingCart,
  LogOut,
  Sparkles,
} from "lucide-react";
import { useAuthState, useLogout } from "../../../hooks/useAuth";

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

const menuItems = [
  {
    key: "/admin/dashboard",
    icon: <LayoutDashboard className="w-4 h-4" />,
    label: "Dashboard",
  },
  {
    key: "/admin/order",
    icon: <ShoppingCart className="w-4 h-4" />,
    label: "Đơn hàng",
  },
];

function AdminLayout() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuthState();
  const logoutMutation = useLogout();

  const handleMenuClick = ({ key }) => {
    navigate(key);
  };

  return (
    <Layout className="min-h-screen bg-[#f5f7f4] h-250">
      <Sider
        width={240}
        className="!bg-gradient-to-b !from-[#18352a] !to-[#11261d]"
      >
        <div className="p-5 border-b border-[#2c4e3f] flex items-center gap-3">
          <div className="rounded-xl bg-[#234b3c] p-2">
            <BookOpen className="w-6 h-6 text-[#d4995f]" />
          </div>
          <div>
            <Title level={5} className="!text-white !m-0">
              Folio & Spine
            </Title>
            <div className="mt-2 inline-flex items-center gap-1 rounded-full border border-[#d4995f]/50 bg-[#234b3c] px-2 py-0.5">
              <Sparkles className="w-3 h-3 text-[#f4c98f]" />
              <span className="text-[10px] uppercase tracking-[0.18em] text-[#f4e6d2]">
                Commerce
              </span>
            </div>
          </div>
        </div>

        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          onClick={handleMenuClick}
          items={menuItems}
          className="!bg-transparent !text-white [&_.ant-menu-item-selected]:!bg-[#254433] [&_.ant-menu-item]:!text-white [&_.ant-menu-item]:!rounded-r-lg [&_.ant-menu-item]:!mx-2 [&_.ant-menu-item]:!px-4"
        />
      </Sider>

      <Layout>
        <Header className="!bg-gradient-to-r !from-white !to-[#f9f4ee] !px-6 !h-20 flex items-center justify-between border-b border-gray-200">
          <div>
            <p className="text-xs uppercase tracking-[0.18em] text-gray-400">
              Quản trị hệ thống
            </p>
            <h2 className="text-xl font-bold text-gray-800 m-0">
              {location.pathname.includes("order") ? "Đơn hàng" : "Dashboard"}
            </h2>
          </div>

          <div className="flex items-center gap-4">
            <div className="flex items-center gap-3 rounded-2xl border border-gray-200 bg-white px-3 py-2 shadow-sm">
              <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-[#f7efe9] text-[#18352a] font-semibold">
                {user?.username?.charAt(0)?.toUpperCase() || "A"}
              </div>
              <div className="text-right">
                <p className="text-xs text-gray-500">Xin chào</p>
                <p className="font-semibold text-gray-800">
                  {user?.username || "Admin"}
                </p>
              </div>
            </div>
            <Button
              icon={<LogOut className="w-4 h-4" />}
              onClick={() =>
                logoutMutation.mutate(undefined, {
                  onSuccess: () => navigate("/login"),
                })
              }
              className="!border-none !bg-[#18352a] !text-white hover:!bg-[#254433]"
            >
              Đăng xuất
            </Button>
          </div>
        </Header>

        <Content className="p-6">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}

export default AdminLayout;
