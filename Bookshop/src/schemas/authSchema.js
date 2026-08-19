import { z } from "zod";

export const loginSchema = z.object({
  username: z.string().min(3, "Tên đăng nhập phải có ít nhất 3 ký tự"),
  password: z.string().min(6, "Mật khẩu phải có ít nhất 6 ký tự"),
  remember: z.boolean().optional().default(false),
});

export const registerSchema = z
  .object({
    username: z.string().min(3, "Tên đăng nhập phải có ít nhất 3 ký tự"),
    email: z.string().email("Email không hợp lệ"),
    password: z.string().min(6, "Mật khẩu phải có ít nhất 6 ký tự"),
    confirmPassword: z
      .string()
      .min(6, "Xác nhận mật khẩu phải có ít nhất 6 ký tự"),
    hoTen: z.string().min(2, "Họ và tên phải có ít nhất 2 ký tự"),
    sdt: z
      .string()
      .regex(/^\d{9,11}$/, "Số điện thoại không hợp lệ")
      .optional(),
    diaChi: z.string().optional(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Mật khẩu không khớp",
    path: ["confirmPassword"],
  });

export const updateProfileSchema = z.object({
  hoTen: z.string().min(2, "Họ và tên phải có ít nhất 2 ký tự"),
  sdt: z
    .string()
    .regex(/^\d{9,11}$/, "Số điện thoại không hợp lệ")
    .optional(),
  diaChi: z.string().optional(),
  email: z.string().email("Email không hợp lệ").optional(),
  avatar: z.string().optional(),
});

export const changePasswordSchema = z
  .object({
    currentPassword: z
      .string()
      .min(6, "Mật khẩu hiện tại phải có ít nhất 6 ký tự"),
    newPassword: z.string().min(6, "Mật khẩu mới phải có ít nhất 6 ký tự"),
    confirmPassword: z
      .string()
      .min(6, "Xác nhận mật khẩu phải có ít nhất 6 ký tự"),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Mật khẩu không khớp",
    path: ["confirmPassword"],
  });
