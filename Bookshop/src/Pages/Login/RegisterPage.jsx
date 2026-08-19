import React, { useState, useEffect } from "react";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { registerSchema } from "../../schemas/authSchema";
import { Divider, message } from "antd";
import {
  Mail,
  Lock,
  BookOpen,
  Eye,
  EyeOff,
  User,
  Phone,
  MapPin,
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useRegisterMutation } from "../../hooks/useAuth";

function RegisterPage() {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [registerSuccess, setRegisterSuccess] = useState(false);

  const registerMutation = useRegisterMutation();
  const { isPending: loading } = registerMutation;

  const {
    control,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      username: "",
      email: "",
      password: "",
      confirmPassword: "",
      hoTen: "",
      sdt: "",
      diaChi: "",
    },
  });

  // Redirect after successful registration
  useEffect(() => {
    if (registerSuccess) {
      message.success("Đăng ký thành công! Vui lòng đăng nhập");
      setTimeout(() => {
        navigate("/login");
      }, 1500);
    }
  }, [registerSuccess, navigate]);

  // Display register errors
  useEffect(() => {
    if (registerMutation.isError) {
      const errorMsg =
        registerMutation.error?.response?.data?.message || "Đăng ký thất bại";
      message.error(errorMsg);
    }
  }, [registerMutation.isError, registerMutation.error]);

  const onSubmit = async (data) => {
    try {
      await registerMutation.mutateAsync({
        username: data.username,
        email: data.email,
        password: data.password,
        hoTen: data.hoTen,
        sdt: data.sdt,
        diaChi: data.diaChi,
      });

      // Chỉ set registerSuccess = true nếu API call thành công
      setRegisterSuccess(true);
    } catch (error) {
      console.error("Register error:", error);
      // Error đã được handle bởi registerMutation
      // Không cần set registerSuccess
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#18352a] to-[#0f1f1a] flex items-center justify-center px-4 py-8">
      {/* Decorative Background Elements */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-0 right-0 w-96 h-96 bg-[#d4995f] opacity-5 rounded-full blur-3xl"></div>
        <div className="absolute bottom-0 left-0 w-80 h-80 bg-[#c18653] opacity-5 rounded-full blur-3xl"></div>
      </div>

      {/* Register Container */}
      <div className="w-full max-w-md relative z-10">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <div className="p-3 bg-[#254433] rounded-xl">
              <BookOpen className="w-8 h-8 text-[#d4995f]" strokeWidth={1.5} />
            </div>
          </div>
          <h1 className="text-3xl font-serif font-bold text-white mb-2">
            Folio & Spine
          </h1>
          <p className="text-gray-400 text-sm">
            Tạo tài khoản để bắt đầu mua sắm sách
          </p>
        </div>

        {/* Register Card */}
        <div className="bg-[#1a2f27] backdrop-blur-sm rounded-xl shadow-2xl p-8 border border-[#254433]">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {/* Username Field */}
            <div className="space-y-2">
              <label className="block text-sm font-medium text-gray-300">
                Tên đăng nhập
              </label>
              <Controller
                name="username"
                control={control}
                render={({ field }) => (
                  <div className="relative">
                    <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                    <input
                      {...field}
                      type="text"
                      placeholder="Nhập tên đăng nhập"
                      className={`w-full bg-[#0f1f1a] text-white placeholder-gray-500 rounded-lg py-2.5 pl-10 pr-4 
                        border transition-all duration-200 focus:outline-none
                        ${
                          errors.username
                            ? "border-red-500 focus:ring-2 focus:ring-red-400"
                            : "border-[#254433] focus:border-[#d4995f] focus:ring-1 focus:ring-[#d4995f]"
                        }`}
                    />
                  </div>
                )}
              />
              {errors.username && (
                <p className="text-red-400 text-sm">
                  {errors.username.message}
                </p>
              )}
            </div>

            {/* Full Name Field (hoTen) */}
            <div className="space-y-2">
              <label className="block text-sm font-medium text-gray-300">
                Họ và tên
              </label>
              <Controller
                name="hoTen"
                control={control}
                render={({ field }) => (
                  <div className="relative">
                    <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                    <input
                      {...field}
                      type="text"
                      placeholder="Nhập tên đầy đủ của bạn"
                      className={`w-full bg-[#0f1f1a] text-white placeholder-gray-500 rounded-lg py-2.5 pl-10 pr-4 
                        border transition-all duration-200 focus:outline-none
                        ${
                          errors.hoTen
                            ? "border-red-500 focus:ring-2 focus:ring-red-400"
                            : "border-[#254433] focus:border-[#d4995f] focus:ring-1 focus:ring-[#d4995f]"
                        }`}
                    />
                  </div>
                )}
              />
              {errors.hoTen && (
                <p className="text-red-400 text-sm">{errors.hoTen.message}</p>
              )}
            </div>

            {/* Email Field */}
            <div className="space-y-2">
              <label className="block text-sm font-medium text-gray-300">
                Email
              </label>
              <Controller
                name="email"
                control={control}
                render={({ field }) => (
                  <div className="relative">
                    <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                    <input
                      {...field}
                      type="email"
                      placeholder="example@bookshop.com"
                      className={`w-full bg-[#0f1f1a] text-white placeholder-gray-500 rounded-lg py-2.5 pl-10 pr-4 
                        border transition-all duration-200 focus:outline-none
                        ${
                          errors.email
                            ? "border-red-500 focus:ring-2 focus:ring-red-400"
                            : "border-[#254433] focus:border-[#d4995f] focus:ring-1 focus:ring-[#d4995f]"
                        }`}
                    />
                  </div>
                )}
              />
              {errors.email && (
                <p className="text-red-400 text-sm">{errors.email.message}</p>
              )}
            </div>

            {/* Password Field */}
            <div className="space-y-2">
              <label className="block text-sm font-medium text-gray-300">
                Mật khẩu
              </label>
              <Controller
                name="password"
                control={control}
                render={({ field }) => (
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                    <input
                      {...field}
                      type={showPassword ? "text" : "password"}
                      placeholder="Nhập mật khẩu"
                      className={`w-full bg-[#0f1f1a] text-white placeholder-gray-500 rounded-lg py-2.5 pl-10 pr-10 
                        border transition-all duration-200 focus:outline-none
                        ${
                          errors.password
                            ? "border-red-500 focus:ring-2 focus:ring-red-400"
                            : "border-[#254433] focus:border-[#d4995f] focus:ring-1 focus:ring-[#d4995f]"
                        }`}
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300"
                    >
                      {showPassword ? (
                        <EyeOff className="w-5 h-5" />
                      ) : (
                        <Eye className="w-5 h-5" />
                      )}
                    </button>
                  </div>
                )}
              />
              {errors.password && (
                <p className="text-red-400 text-sm">
                  {errors.password.message}
                </p>
              )}
            </div>

            {/* Confirm Password Field */}
            <div className="space-y-2">
              <label className="block text-sm font-medium text-gray-300">
                Xác nhận mật khẩu
              </label>
              <Controller
                name="confirmPassword"
                control={control}
                render={({ field }) => (
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                    <input
                      {...field}
                      type={showConfirmPassword ? "text" : "password"}
                      placeholder="Xác nhận mật khẩu"
                      className={`w-full bg-[#0f1f1a] text-white placeholder-gray-500 rounded-lg py-2.5 pl-10 pr-10 
                        border transition-all duration-200 focus:outline-none
                        ${
                          errors.confirmPassword
                            ? "border-red-500 focus:ring-2 focus:ring-red-400"
                            : "border-[#254433] focus:border-[#d4995f] focus:ring-1 focus:ring-[#d4995f]"
                        }`}
                    />
                    <button
                      type="button"
                      onClick={() =>
                        setShowConfirmPassword(!showConfirmPassword)
                      }
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300"
                    >
                      {showConfirmPassword ? (
                        <EyeOff className="w-5 h-5" />
                      ) : (
                        <Eye className="w-5 h-5" />
                      )}
                    </button>
                  </div>
                )}
              />
              {errors.confirmPassword && (
                <p className="text-red-400 text-sm">
                  {errors.confirmPassword.message}
                </p>
              )}
            </div>

            {/* Phone Field (sdt) - Optional */}
            <div className="space-y-2">
              <label className="block text-sm font-medium text-gray-300">
                Số điện thoại (không bắt buộc)
              </label>
              <Controller
                name="sdt"
                control={control}
                render={({ field }) => (
                  <div className="relative">
                    <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                    <input
                      {...field}
                      type="tel"
                      placeholder="0123456789"
                      className={`w-full bg-[#0f1f1a] text-white placeholder-gray-500 rounded-lg py-2.5 pl-10 pr-4 
                        border transition-all duration-200 focus:outline-none
                        ${
                          errors.sdt
                            ? "border-red-500 focus:ring-2 focus:ring-red-400"
                            : "border-[#254433] focus:border-[#d4995f] focus:ring-1 focus:ring-[#d4995f]"
                        }`}
                    />
                  </div>
                )}
              />
              {errors.sdt && (
                <p className="text-red-400 text-sm">{errors.sdt.message}</p>
              )}
            </div>

            {/* Address Field (diaChi) - Optional */}
            <div className="space-y-2">
              <label className="block text-sm font-medium text-gray-300">
                Địa chỉ (không bắt buộc)
              </label>
              <Controller
                name="diaChi"
                control={control}
                render={({ field }) => (
                  <div className="relative">
                    <MapPin className="absolute left-3 top-3 w-5 h-5 text-gray-500" />
                    <textarea
                      {...field}
                      placeholder="Nhập địa chỉ của bạn"
                      rows="3"
                      className="w-full bg-[#0f1f1a] text-white placeholder-gray-500 rounded-lg py-2.5 pl-10 pr-4 
                        border border-[#254433] focus:border-[#d4995f] focus:ring-1 focus:ring-[#d4995f]
                        transition-all duration-200 focus:outline-none resize-none"
                    />
                  </div>
                )}
              />
            </div>

            {/* Register Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-[#c18653] hover:bg-[#a67144] text-white font-semibold py-2.5 rounded-lg 
                transition-all duration-200 border-none cursor-pointer disabled:opacity-70 disabled:cursor-not-allowed mt-6"
            >
              {loading ? "Đang đăng ký..." : "Đăng ký"}
            </button>

            {/* Divider */}
            <div className="relative my-6">
              <Divider className="!my-0 !bg-[#254433]">
                <span className="text-gray-500 text-xs">HOẶC</span>
              </Divider>
            </div>

            {/* Social Register Buttons */}
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                className="flex items-center justify-center gap-2 bg-[#254433] hover:bg-[#2d5244] 
                  text-gray-300 py-2.5 rounded-lg transition-colors border border-[#2d5244]"
              >
                <svg
                  className="w-5 h-5"
                  viewBox="0 0 24 24"
                  fill="currentColor"
                >
                  <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
                  <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
                  <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" />
                  <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" />
                </svg>
                <span className="text-sm font-medium">Google</span>
              </button>
              <button
                type="button"
                className="flex items-center justify-center gap-2 bg-[#254433] hover:bg-[#2d5244] 
                  text-gray-300 py-2.5 rounded-lg transition-colors border border-[#2d5244]"
              >
                <svg
                  className="w-5 h-5"
                  viewBox="0 0 24 24"
                  fill="currentColor"
                >
                  <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
                </svg>
                <span className="text-sm font-medium">Facebook</span>
              </button>
            </div>
          </form>

          {/* Login Link */}
          <div className="mt-8 text-center pt-6 border-t border-[#254433]">
            <p className="text-gray-400 text-sm">
              Đã có tài khoản?{" "}
              <a
                href="/login"
                className="text-[#d4995f] hover:text-[#e6b87f] font-medium transition-colors"
              >
                Đăng nhập
              </a>
            </p>
          </div>
        </div>

        {/* Footer Note */}
        <p className="text-center text-gray-500 text-xs mt-6">
          Bằng cách đăng ký, bạn đồng ý với{" "}
          <a href="#" className="text-[#d4995f] hover:underline">
            Điều khoản sử dụng
          </a>{" "}
          và{" "}
          <a href="#" className="text-[#d4995f] hover:underline">
            Chính sách bảo mật
          </a>
        </p>
      </div>
    </div>
  );
}

export default RegisterPage;
