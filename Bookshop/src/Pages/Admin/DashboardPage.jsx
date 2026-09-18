import React, { useEffect, useMemo, useState } from "react";
import { useQueries, useQueryClient } from "@tanstack/react-query";
import {
  Card,
  Table,
  Button,
  Space,
  Tag,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  Switch,
  Upload,
  message,
  Tabs,
  Statistic,
  Spin,
} from "antd";
import {
  BookOpen,
  LayoutDashboard,
  Tags,
  ShoppingCart,
  Users,
  Building2,
  PenTool,
  Package,
} from "lucide-react";
import sachService from "../../services/Sachservice";
import theloaiService from "../../services/Theloaiservice";
import hoaDonService from "../../services/Hoadonservice";
import khachHangService from "../../services/KhachHangservice";
import nhaXuatBanService from "../../services/NhaXuatBanservice";
import tacGiaService from "../../services/TacGiaservice";
import nhaCungCapService from "../../services/NhaCungCapservice";

const { TabPane } = Tabs;

function DashboardPage() {
  const [bookModalOpen, setBookModalOpen] = useState(false);
  const [categoryModalOpen, setCategoryModalOpen] = useState(false);
  const [publisherModalOpen, setPublisherModalOpen] = useState(false);
  const [authorModalOpen, setAuthorModalOpen] = useState(false);
  const [supplierModalOpen, setSupplierModalOpen] = useState(false);
  const [bookDetailOpen, setBookDetailOpen] = useState(false);

  const [editingBook, setEditingBook] = useState(null);
  const [editingCategory, setEditingCategory] = useState(null);
  const [editingPublisher, setEditingPublisher] = useState(null);
  const [editingAuthor, setEditingAuthor] = useState(null);
  const [editingSupplier, setEditingSupplier] = useState(null);
  const [selectedBook, setSelectedBook] = useState(null);

  const [bookForm] = Form.useForm();
  const [categoryForm] = Form.useForm();
  const [publisherForm] = Form.useForm();
  const [authorForm] = Form.useForm();
  const [supplierForm] = Form.useForm();
  const [bookImageFile, setBookImageFile] = useState(null);

  const queryClient = useQueryClient();

  const normalizeListResponse = (data) => {
    if (Array.isArray(data)) {
      return data;
    }

    if (Array.isArray(data?.content)) {
      return data.content;
    }

    if (Array.isArray(data?.items)) {
      return data.items;
    }

    return [];
  };

  const dashboardQueries = useQueries({
    queries: [
      {
        queryKey: ["dashboard", "books"],
        queryFn: () => sachService.getAll(),
      },
      {
        queryKey: ["dashboard", "categories"],
        queryFn: () => theloaiService.getAll(),
      },
      {
        queryKey: ["dashboard", "orders"],
        queryFn: () => hoaDonService.getAll(),
      },
      {
        queryKey: ["dashboard", "customers"],
        queryFn: () => khachHangService.getAll(),
      },
      {
        queryKey: ["dashboard", "publishers"],
        queryFn: () => nhaXuatBanService.getAll(),
      },
      {
        queryKey: ["dashboard", "authors"],
        queryFn: () => tacGiaService.getAll(),
      },
      {
        queryKey: ["dashboard", "suppliers"],
        queryFn: () => nhaCungCapService.getAll(),
      },
    ],
  });

  const [
    booksQuery,
    categoriesQuery,
    ordersQuery,
    customersQuery,
    publishersQuery,
    authorsQuery,
    suppliersQuery,
  ] = dashboardQueries;

  const books = normalizeListResponse(booksQuery.data);
  const categories = normalizeListResponse(categoriesQuery.data);
  const orders = normalizeListResponse(ordersQuery.data);
  const customers = normalizeListResponse(customersQuery.data);
  const publishers = normalizeListResponse(publishersQuery.data);
  const authors = normalizeListResponse(authorsQuery.data);
  const suppliers = normalizeListResponse(suppliersQuery.data);

  const loading = dashboardQueries.some((query) => query.isLoading);

  const failedEndpoints = dashboardQueries
    .map((query, index) => ({ query, index }))
    .filter(({ query }) => query.isError)
    .map(({ index }) => {
      const endpoints = [
        "sach",
        "the-loai",
        "hoa-don",
        "khach-hang",
        "nha-xuat-ban",
        "tac-gia",
        "nha-cung-cap",
      ];
      return endpoints[index];
    });

  const fetchData = async () => {
    await queryClient.invalidateQueries({ queryKey: ["dashboard"] });
  };

  useEffect(() => {
    if (failedEndpoints.length > 0) {
      console.warn("Some admin endpoints failed to load:", failedEndpoints);

      if (failedEndpoints.length === dashboardQueries.length) {
        message.error("Không thể tải dữ liệu quản trị");
      } else {
        message.warning("Một số dữ liệu quản trị chưa tải được");
      }
    }
  }, [failedEndpoints, dashboardQueries.length]);

  const stats = useMemo(() => {
    const totalRevenue = orders.reduce(
      (sum, order) => sum + Number(order.tongTien || 0),
      0,
    );

    return {
      totalBooks: books.length,
      totalCategories: categories.length,
      totalOrders: orders.length,
      totalCustomers: customers.length,
      totalPublishers: publishers.length,
      totalAuthors: authors.length,
      totalSuppliers: suppliers.length,
      totalRevenue,
      lowStock: books.filter((book) => Number(book.soLuongTon || 0) <= 5)
        .length,
    };
  }, [books, categories, orders, customers, publishers, authors, suppliers]);

  const handleOpenBookModal = (record = null) => {
    setEditingBook(record);
    setBookImageFile(null);
    setBookModalOpen(true);
    bookForm.setFieldsValue({
      tenSach: record?.tenSach || "",
      moTa: record?.moTa || "",
      giaBan: record?.giaBan || 0,
      soLuongTon: record?.soLuongTon || 0,
      hinhAnh: record?.hinhAnh || "",
      namXuatBan: record?.namXuatBan || new Date().getFullYear(),
      dangKinhDoanh: record?.dangKinhDoanh ?? true,
      theLoai: record?.theLoai?.id || undefined,
      nhaXuatBan: record?.nhaXuatBan?.id || undefined,
      tacGias: record?.tacGias?.map((author) => author.id) || [],
    });
  };

  const handleOpenBookDetail = (record) => {
    setSelectedBook(record);
    setBookDetailOpen(true);
  };

  const handleBookSubmit = async (values) => {
    try {
      const payload = {
        tenSach: values.tenSach,
        moTa: values.moTa || "",
        giaBan: Number(values.giaBan),
        soLuongTon: Number(values.soLuongTon),
        hinhAnh: values.hinhAnh || editingBook?.hinhAnh || "",
        namXuatBan: Number(values.namXuatBan),
        dangKinhDoanh: values.dangKinhDoanh,
        theLoai: values.theLoai ? { id: values.theLoai } : null,
        nhaXuatBan: values.nhaXuatBan ? { id: values.nhaXuatBan } : null,
        tacGias: (values.tacGias || []).map((id) => ({ id })),
      };

      if (bookImageFile) {
        if (editingBook) {
          await sachService.updateWithImage(
            editingBook.id,
            payload,
            bookImageFile,
          );
          message.success("Cập nhật sách thành công");
        } else {
          await sachService.createWithImage(payload, bookImageFile);
          message.success("Thêm sách thành công");
        }
      } else if (editingBook) {
        await sachService.update(editingBook.id, payload);
        message.success("Cập nhật sách thành công");
      } else {
        await sachService.create(payload);
        message.success("Thêm sách thành công");
      }

      setBookModalOpen(false);
      setEditingBook(null);
      setBookImageFile(null);
      bookForm.resetFields();
      fetchData();
    } catch (error) {
      console.error("Book submit error:", error);
      message.error("Có lỗi xảy ra khi lưu sách");
    }
  };

  const handleDeleteBook = async (id) => {
    try {
      await sachService.delete(id);
      message.success("Xóa sách thành công");
      fetchData();
    } catch (error) {
      console.error("Delete book error:", error);
      message.error("Không thể xóa sách");
    }
  };

  const handleOpenCategoryModal = (record = null) => {
    setEditingCategory(record);
    setCategoryModalOpen(true);
    categoryForm.setFieldsValue({
      tenTheLoai: record?.tenTheLoai || "",
      moTa: record?.moTa || "",
    });
  };

  const handleCategorySubmit = async (values) => {
    try {
      const payload = {
        tenTheLoai: values.tenTheLoai,
        moTa: values.moTa,
      };

      if (editingCategory) {
        await theloaiService.update(editingCategory.id, payload);
        message.success("Cập nhật thể loại thành công");
      } else {
        await theloaiService.create(payload);
        message.success("Thêm thể loại thành công");
      }

      setCategoryModalOpen(false);
      setEditingCategory(null);
      categoryForm.resetFields();
      fetchData();
    } catch (error) {
      console.error("Category submit error:", error);
      message.error("Có lỗi xảy ra khi lưu thể loại");
    }
  };

  const handleDeleteCategory = async (id) => {
    try {
      await theloaiService.delete(id);
      message.success("Xóa thể loại thành công");
      fetchData();
    } catch (error) {
      console.error("Delete category error:", error);
      message.error("Không thể xóa thể loại");
    }
  };

  const handleOpenPublisherModal = (record = null) => {
    setEditingPublisher(record);
    setPublisherModalOpen(true);
    publisherForm.setFieldsValue({
      tenNxb: record?.tenNxb || "",
      diaChi: record?.diaChi || "",
    });
  };

  const handlePublisherSubmit = async (values) => {
    try {
      const payload = {
        tenNxb: values.tenNxb,
        diaChi: values.diaChi,
      };

      if (editingPublisher) {
        await nhaXuatBanService.update(editingPublisher.id, payload);
        message.success("Cập nhật NXB thành công");
      } else {
        await nhaXuatBanService.create(payload);
        message.success("Thêm NXB thành công");
      }

      setPublisherModalOpen(false);
      setEditingPublisher(null);
      publisherForm.resetFields();
      fetchData();
    } catch (error) {
      console.error("Publisher submit error:", error);
      message.error("Có lỗi xảy ra khi lưu NXB");
    }
  };

  const handleDeletePublisher = async (id) => {
    try {
      await nhaXuatBanService.delete(id);
      message.success("Xóa NXB thành công");
      fetchData();
    } catch (error) {
      console.error("Delete publisher error:", error);
      message.error("Không thể xóa NXB");
    }
  };

  const handleOpenAuthorModal = (record = null) => {
    setEditingAuthor(record);
    setAuthorModalOpen(true);
    authorForm.setFieldsValue({
      hoTen: record?.hoTen || "",
      quocTich: record?.quocTich || "",
      tieuSu: record?.tieuSu || "",
    });
  };

  const handleAuthorSubmit = async (values) => {
    try {
      const payload = {
        hoTen: values.hoTen,
        quocTich: values.quocTich,
        tieuSu: values.tieuSu || "",
      };

      if (editingAuthor) {
        await tacGiaService.update(editingAuthor.id, payload);
        message.success("Cập nhật tác giả thành công");
      } else {
        await tacGiaService.create(payload);
        message.success("Thêm tác giả thành công");
      }

      setAuthorModalOpen(false);
      setEditingAuthor(null);
      authorForm.resetFields();
      fetchData();
    } catch (error) {
      console.error("Author submit error:", error);
      message.error("Có lỗi xảy ra khi lưu tác giả");
    }
  };

  const handleDeleteAuthor = async (id) => {
    try {
      await tacGiaService.delete(id);
      message.success("Xóa tác giả thành công");
      fetchData();
    } catch (error) {
      console.error("Delete author error:", error);
      message.error("Không thể xóa tác giả");
    }
  };

  const handleOpenSupplierModal = (record = null) => {
    setEditingSupplier(record);
    setSupplierModalOpen(true);
    supplierForm.setFieldsValue({
      tenNcc: record?.tenNcc || "",
      sdt: record?.sdt || "",
      diaChi: record?.diaChi || "",
    });
  };

  const handleSupplierSubmit = async (values) => {
    try {
      const payload = {
        tenNcc: values.tenNcc,
        sdt: values.sdt,
        diaChi: values.diaChi,
      };

      if (editingSupplier) {
        await nhaCungCapService.update(editingSupplier.id, payload);
        message.success("Cập nhật nhà cung cấp thành công");
      } else {
        await nhaCungCapService.create(payload);
        message.success("Thêm nhà cung cấp thành công");
      }

      setSupplierModalOpen(false);
      setEditingSupplier(null);
      supplierForm.resetFields();
      fetchData();
    } catch (error) {
      console.error("Supplier submit error:", error);
      message.error("Có lỗi xảy ra khi lưu nhà cung cấp");
    }
  };

  const handleDeleteSupplier = async (id) => {
    try {
      await nhaCungCapService.delete(id);
      message.success("Xóa nhà cung cấp thành công");
      fetchData();
    } catch (error) {
      console.error("Delete supplier error:", error);
      message.error("Không thể xóa nhà cung cấp");
    }
  };

  const bookColumns = [
    {
      title: "Sách",
      dataIndex: "tenSach",
      key: "tenSach",
      render: (_, record) => (
        <Space>
          <img
            src={
              record.hinhAnh ||
              "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=200&h=260"
            }
            alt={record.tenSach}
            className="w-12 h-16 object-cover rounded"
          />
          <div>
            <div className="font-semibold">{record.tenSach}</div>
            <div className="text-xs text-gray-500">
              {record.theLoai?.tenTheLoai || "Chưa phân loại"}
            </div>
          </div>
        </Space>
      ),
    },
    {
      title: "Giá bán",
      dataIndex: "giaBan",
      key: "giaBan",
      render: (value) =>
        Number(value).toLocaleString("vi-VN", {
          style: "currency",
          currency: "VND",
        }),
    },
    {
      title: "Tồn kho",
      dataIndex: "soLuongTon",
      key: "soLuongTon",
      render: (value) => (
        <Tag color={Number(value) <= 5 ? "red" : "green"}>{value}</Tag>
      ),
    },
    {
      title: "Năm XB",
      dataIndex: "namXuatBan",
      key: "namXuatBan",
    },
    {
      title: "Mô tả",
      dataIndex: "moTa",
      key: "moTa",
      ellipsis: true,
      render: (value) => value || "-",
    },
    {
      title: "Trạng thái",
      dataIndex: "dangKinhDoanh",
      key: "dangKinhDoanh",
      render: (value) => (
        <Tag color={value ? "green" : "default"}>
          {value ? "Đang kinh doanh" : "Tạm ngừng"}
        </Tag>
      ),
    },
    {
      title: "Thao tác",
      key: "action",
      render: (_, record) => (
        <Space>
          <Button size="small" onClick={() => handleOpenBookDetail(record)}>
            Chi tiết
          </Button>
          <Button size="small" onClick={() => handleOpenBookModal(record)}>
            Sửa
          </Button>
          <Button
            size="small"
            danger
            onClick={() => handleDeleteBook(record.id)}
          >
            Xóa
          </Button>
        </Space>
      ),
    },
  ];

  const categoryColumns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "Tên thể loại",
      dataIndex: "tenTheLoai",
      key: "tenTheLoai",
    },
    {
      title: "Mô tả",
      dataIndex: "moTa",
      key: "moTa",
      render: (value) => value || "-",
    },
    {
      title: "Thao tác",
      key: "action",
      render: (_, record) => (
        <Space>
          <Button size="small" onClick={() => handleOpenCategoryModal(record)}>
            Sửa
          </Button>
          <Button
            size="small"
            danger
            onClick={() => handleDeleteCategory(record.id)}
          >
            Xóa
          </Button>
        </Space>
      ),
    },
  ];

  const publisherColumns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "Tên NXB",
      dataIndex: "tenNxb",
      key: "tenNxb",
    },
    {
      title: "Địa chỉ",
      dataIndex: "diaChi",
      key: "diaChi",
      render: (value) => value || "-",
    },
    {
      title: "Thao tác",
      key: "action",
      render: (_, record) => (
        <Space>
          <Button size="small" onClick={() => handleOpenPublisherModal(record)}>
            Sửa
          </Button>
          <Button
            size="small"
            danger
            onClick={() => handleDeletePublisher(record.id)}
          >
            Xóa
          </Button>
        </Space>
      ),
    },
  ];

  const authorColumns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "Họ tên",
      dataIndex: "hoTen",
      key: "hoTen",
    },
    {
      title: "Quốc tịch",
      dataIndex: "quocTich",
      key: "quocTich",
      render: (value) => value || "-",
    },
    {
      title: "Tiểu sử",
      dataIndex: "tieuSu",
      key: "tieuSu",
      ellipsis: true,
      render: (value) => value || "-",
    },
    {
      title: "Thao tác",
      key: "action",
      render: (_, record) => (
        <Space>
          <Button size="small" onClick={() => handleOpenAuthorModal(record)}>
            Sửa
          </Button>
          <Button
            size="small"
            danger
            onClick={() => handleDeleteAuthor(record.id)}
          >
            Xóa
          </Button>
        </Space>
      ),
    },
  ];

  const supplierColumns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "Tên nhà cung cấp",
      dataIndex: "tenNcc",
      key: "tenNcc",
    },
    {
      title: "Số điện thoại",
      dataIndex: "sdt",
      key: "sdt",
      render: (value) => value || "-",
    },
    {
      title: "Địa chỉ",
      dataIndex: "diaChi",
      key: "diaChi",
      render: (value) => value || "-",
    },
    {
      title: "Thao tác",
      key: "action",
      render: (_, record) => (
        <Space>
          <Button size="small" onClick={() => handleOpenSupplierModal(record)}>
            Sửa
          </Button>
          <Button
            size="small"
            danger
            onClick={() => handleDeleteSupplier(record.id)}
          >
            Xóa
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-7 gap-4">
        <Card className="!rounded-2xl !border-0 !shadow-sm">
          <Statistic
            title="Tổng sách"
            value={stats.totalBooks}
            prefix={<BookOpen className="w-4 h-4 mr-2 text-[#c18653]" />}
          />
        </Card>
        <Card className="!rounded-2xl !border-0 !shadow-sm">
          <Statistic
            title="Thể loại"
            value={stats.totalCategories}
            prefix={<Tags className="w-4 h-4 mr-2 text-[#1b3627]" />}
          />
        </Card>
        <Card className="!rounded-2xl !border-0 !shadow-sm">
          <Statistic
            title="NXB"
            value={stats.totalPublishers}
            prefix={<Building2 className="w-4 h-4 mr-2 text-[#d4995f]" />}
          />
        </Card>
        <Card className="!rounded-2xl !border-0 !shadow-sm">
          <Statistic
            title="Tác giả"
            value={stats.totalAuthors}
            prefix={<PenTool className="w-4 h-4 mr-2 text-[#a67144]" />}
          />
        </Card>
        <Card className="!rounded-2xl !border-0 !shadow-sm">
          <Statistic
            title="Nhà cung cấp"
            value={stats.totalSuppliers}
            prefix={<Package className="w-4 h-4 mr-2 text-[#18352a]" />}
          />
        </Card>
        <Card className="!rounded-2xl !border-0 !shadow-sm">
          <Statistic
            title="Đơn hàng"
            value={stats.totalOrders}
            prefix={<ShoppingCart className="w-4 h-4 mr-2 text-[#c18653]" />}
          />
        </Card>
        <Card className="!rounded-2xl !border-0 !shadow-sm">
          <Statistic
            title="Khách hàng"
            value={stats.totalCustomers}
            prefix={<Users className="w-4 h-4 mr-2 text-[#254433]" />}
          />
        </Card>
      </div>

      <Card className="!rounded-2xl !border-0 !shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-lg font-semibold mb-1">Tình trạng kho</h3>
            <p className="text-sm text-gray-500">
              Có {stats.lowStock} đầu sách sắp hết hàng
            </p>
          </div>
          <Button
            type="primary"
            className="!bg-[#18352a] !border-[#18352a] hover:!bg-[#254433]"
            onClick={() => handleOpenBookModal()}
          >
            + Thêm sách
          </Button>
        </div>

        <Tabs defaultActiveKey="books">
          <TabPane tab="Quản lý sách" key="books">
            <Spin spinning={loading}>
              <Table
                columns={bookColumns}
                dataSource={books}
                rowKey="id"
                pagination={{ pageSize: 6 }}
              />
            </Spin>
          </TabPane>

          <TabPane tab="Quản lý thể loại" key="categories">
            <div className="flex justify-end mb-4">
              <Button type="primary" onClick={() => handleOpenCategoryModal()}>
                + Thêm thể loại
              </Button>
            </div>
            <Spin spinning={loading}>
              <Table
                columns={categoryColumns}
                dataSource={categories}
                rowKey="id"
                pagination={{ pageSize: 6 }}
              />
            </Spin>
          </TabPane>

          <TabPane tab="Quản lý NXB" key="publishers">
            <div className="flex justify-end mb-4">
              <Button type="primary" onClick={() => handleOpenPublisherModal()}>
                + Thêm NXB
              </Button>
            </div>
            <Spin spinning={loading}>
              <Table
                columns={publisherColumns}
                dataSource={publishers}
                rowKey="id"
                pagination={{ pageSize: 6 }}
              />
            </Spin>
          </TabPane>

          <TabPane tab="Quản lý tác giả" key="authors">
            <div className="flex justify-end mb-4">
              <Button type="primary" onClick={() => handleOpenAuthorModal()}>
                + Thêm tác giả
              </Button>
            </div>
            <Spin spinning={loading}>
              <Table
                columns={authorColumns}
                dataSource={authors}
                rowKey="id"
                pagination={{ pageSize: 6 }}
              />
            </Spin>
          </TabPane>

          <TabPane tab="Quản lý nhà cung cấp" key="suppliers">
            <div className="flex justify-end mb-4">
              <Button type="primary" onClick={() => handleOpenSupplierModal()}>
                + Thêm nhà cung cấp
              </Button>
            </div>
            <Spin spinning={loading}>
              <Table
                columns={supplierColumns}
                dataSource={suppliers}
                rowKey="id"
                pagination={{ pageSize: 6 }}
              />
            </Spin>
          </TabPane>
        </Tabs>
      </Card>

      <Modal
        title={editingBook ? "Cập nhật sách" : "Thêm sách mới"}
        open={bookModalOpen}
        onCancel={() => {
          setBookModalOpen(false);
          setEditingBook(null);
          bookForm.resetFields();
        }}
        footer={null}
        width={760}
      >
        <Form form={bookForm} layout="vertical" onFinish={handleBookSubmit}>
          <Form.Item
            label="Tên sách"
            name="tenSach"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>

          <Form.Item label="Mô tả" name="moTa">
            <Input.TextArea rows={4} />
          </Form.Item>

          <div className="grid grid-cols-2 gap-4">
            <Form.Item
              label="Giá bán"
              name="giaBan"
              rules={[{ required: true }]}
            >
              <InputNumber min={0} className="w-full" />
            </Form.Item>
            <Form.Item
              label="Tồn kho"
              name="soLuongTon"
              rules={[{ required: true }]}
            >
              <InputNumber min={0} className="w-full" />
            </Form.Item>
          </div>

          <div className="grid grid-cols-3 gap-4">
            <Form.Item
              label="Năm xuất bản"
              name="namXuatBan"
              rules={[{ required: true }]}
            >
              <InputNumber min={1900} className="w-full" />
            </Form.Item>
            <Form.Item label="Thể loại" name="theLoai">
              <Select
                allowClear
                placeholder="Chọn thể loại"
                options={categories.map((category) => ({
                  value: category.id,
                  label: category.tenTheLoai,
                }))}
              />
            </Form.Item>
            <Form.Item label="NXB" name="nhaXuatBan">
              <Select
                allowClear
                placeholder="Chọn NXB"
                options={publishers.map((publisher) => ({
                  value: publisher.id,
                  label: publisher.tenNxb,
                }))}
              />
            </Form.Item>
          </div>

          <Form.Item label="Tác giả" name="tacGias">
            <Select
              mode="multiple"
              allowClear
              placeholder="Chọn tác giả"
              options={authors.map((author) => ({
                value: author.id,
                label: author.hoTen,
              }))}
            />
          </Form.Item>

          <Form.Item label="Hình ảnh" name="hinhAnh">
            <div className="space-y-3">
              <Upload
                accept="image/*"
                beforeUpload={(file) => {
                  setBookImageFile(file);
                  bookForm.setFieldValue("hinhAnh", file.name);
                  return false;
                }}
                showUploadList={false}
              >
                <Button>Chọn file ảnh</Button>
              </Upload>

              <Input
                value={bookForm.getFieldValue("hinhAnh") || ""}
                onChange={(e) => {
                  bookForm.setFieldValue("hinhAnh", e.target.value);
                }}
                placeholder="Hoặc nhập URL ảnh"
              />
            </div>
          </Form.Item>

          <Form.Item
            label="Đang kinh doanh"
            name="dangKinhDoanh"
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>

          <div className="flex justify-end gap-2">
            <Button onClick={() => setBookModalOpen(false)}>Hủy</Button>
            <Button type="primary" htmlType="submit">
              {editingBook ? "Cập nhật" : "Thêm mới"}
            </Button>
          </div>
        </Form>
      </Modal>

      <Modal
        title={editingCategory ? "Cập nhật thể loại" : "Thêm thể loại mới"}
        open={categoryModalOpen}
        onCancel={() => {
          setCategoryModalOpen(false);
          setEditingCategory(null);
          categoryForm.resetFields();
        }}
        footer={null}
      >
        <Form
          form={categoryForm}
          layout="vertical"
          onFinish={handleCategorySubmit}
        >
          <Form.Item
            label="Tên thể loại"
            name="tenTheLoai"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="Mô tả" name="moTa">
            <Input.TextArea rows={4} />
          </Form.Item>
          <div className="flex justify-end gap-2">
            <Button onClick={() => setCategoryModalOpen(false)}>Hủy</Button>
            <Button type="primary" htmlType="submit">
              {editingCategory ? "Cập nhật" : "Thêm mới"}
            </Button>
          </div>
        </Form>
      </Modal>

      <Modal
        title={editingPublisher ? "Cập nhật NXB" : "Thêm NXB mới"}
        open={publisherModalOpen}
        onCancel={() => {
          setPublisherModalOpen(false);
          setEditingPublisher(null);
          publisherForm.resetFields();
        }}
        footer={null}
      >
        <Form
          form={publisherForm}
          layout="vertical"
          onFinish={handlePublisherSubmit}
        >
          <Form.Item label="Tên NXB" name="tenNxb" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item label="Địa chỉ" name="diaChi">
            <Input.TextArea rows={4} />
          </Form.Item>
          <div className="flex justify-end gap-2">
            <Button onClick={() => setPublisherModalOpen(false)}>Hủy</Button>
            <Button type="primary" htmlType="submit">
              {editingPublisher ? "Cập nhật" : "Thêm mới"}
            </Button>
          </div>
        </Form>
      </Modal>

      <Modal
        title={editingAuthor ? "Cập nhật tác giả" : "Thêm tác giả mới"}
        open={authorModalOpen}
        onCancel={() => {
          setAuthorModalOpen(false);
          setEditingAuthor(null);
          authorForm.resetFields();
        }}
        footer={null}
      >
        <Form form={authorForm} layout="vertical" onFinish={handleAuthorSubmit}>
          <Form.Item label="Họ tên" name="hoTen" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item label="Quốc tịch" name="quocTich">
            <Input />
          </Form.Item>
          <Form.Item label="Tiểu sử" name="tieuSu">
            <Input.TextArea rows={5} />
          </Form.Item>
          <div className="flex justify-end gap-2">
            <Button onClick={() => setAuthorModalOpen(false)}>Hủy</Button>
            <Button type="primary" htmlType="submit">
              {editingAuthor ? "Cập nhật" : "Thêm mới"}
            </Button>
          </div>
        </Form>
      </Modal>

      <Modal
        title={
          editingSupplier ? "Cập nhật nhà cung cấp" : "Thêm nhà cung cấp mới"
        }
        open={supplierModalOpen}
        onCancel={() => {
          setSupplierModalOpen(false);
          setEditingSupplier(null);
          supplierForm.resetFields();
        }}
        footer={null}
      >
        <Form
          form={supplierForm}
          layout="vertical"
          onFinish={handleSupplierSubmit}
        >
          <Form.Item
            label="Tên nhà cung cấp"
            name="tenNcc"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="Số điện thoại" name="sdt">
            <Input />
          </Form.Item>
          <Form.Item label="Địa chỉ" name="diaChi">
            <Input.TextArea rows={4} />
          </Form.Item>
          <div className="flex justify-end gap-2">
            <Button onClick={() => setSupplierModalOpen(false)}>Hủy</Button>
            <Button type="primary" htmlType="submit">
              {editingSupplier ? "Cập nhật" : "Thêm mới"}
            </Button>
          </div>
        </Form>
      </Modal>

      <Modal
        title="Chi tiết sách"
        open={bookDetailOpen}
        onCancel={() => {
          setBookDetailOpen(false);
          setSelectedBook(null);
        }}
        footer={null}
        width={900}
      >
        {selectedBook && (
          <div className="space-y-5">
            <div className="flex gap-5">
              <img
                src={
                  selectedBook.hinhAnh ||
                  "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=200&h=260"
                }
                alt={selectedBook.tenSach}
                className="w-32 h-44 object-cover rounded"
              />

              <div className="flex-1 space-y-2">
                <h3 className="text-2xl font-bold">{selectedBook.tenSach}</h3>
                <div className="grid grid-cols-2 gap-3 text-sm">
                  <div>
                    <span className="text-gray-500">Thể loại:</span>{" "}
                    <span className="font-semibold">
                      {selectedBook.theLoai?.tenTheLoai || "Chưa phân loại"}
                    </span>
                  </div>
                  <div>
                    <span className="text-gray-500">NXB:</span>{" "}
                    <span className="font-semibold">
                      {selectedBook.nhaXuatBan?.tenNxb || "Chưa cập nhật"}
                    </span>
                  </div>
                  <div>
                    <span className="text-gray-500">Tác giả:</span>{" "}
                    <span className="font-semibold">
                      {selectedBook.tacGias
                        ?.map((item) => item.hoTen || item.tenTacGia)
                        .join(", ") || "Chưa cập nhật"}
                    </span>
                  </div>
                  <div>
                    <span className="text-gray-500">Năm xuất bản:</span>{" "}
                    <span className="font-semibold">
                      {selectedBook.namXuatBan || "Chưa cập nhật"}
                    </span>
                  </div>
                  <div>
                    <span className="text-gray-500">Giá bán:</span>{" "}
                    <span className="font-semibold text-[#c18653]">
                      {Number(selectedBook.giaBan || 0).toLocaleString(
                        "vi-VN",
                        {
                          style: "currency",
                          currency: "VND",
                        },
                      )}
                    </span>
                  </div>
                  <div>
                    <span className="text-gray-500">Tồn kho:</span>{" "}
                    <span className="font-semibold">
                      {selectedBook.soLuongTon || 0}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <div className="border-t pt-4">
              <h4 className="font-semibold mb-2">Thông tin bổ sung</h4>
              <div className="mb-4 text-sm">
                <span className="text-gray-500">Mô tả:</span>{" "}
                <span>{selectedBook.moTa || "Chưa cập nhật"}</span>
              </div>
              <div className="grid grid-cols-2 gap-3 text-sm">
                <div>
                  <span className="text-gray-500">Trạng thái:</span>{" "}
                  <Tag color={selectedBook.dangKinhDoanh ? "green" : "default"}>
                    {selectedBook.dangKinhDoanh
                      ? "Đang kinh doanh"
                      : "Tạm ngừng"}
                  </Tag>
                </div>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}

export default DashboardPage;
