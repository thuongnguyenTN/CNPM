package com.cnpm.controller.admin;

import com.cnpm.entity.Coupon;
import com.cnpm.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    // Hiển thị danh sách mã giảm giá
    @GetMapping
    public String listCoupons(Model model) {
        model.addAttribute("coupons", couponService.findAll());
        model.addAttribute("coupon", new Coupon()); // Đối tượng rỗng cho form
        return "admin/coupons";
    }

    // Xử lý lưu (thêm mới hoặc cập nhật)
    @PostMapping("/save")
    public String saveCoupon(@ModelAttribute("coupon") Coupon coupon, RedirectAttributes redirectAttributes) {
        try {
            couponService.save(coupon);
            redirectAttributes.addFlashAttribute("success", "Lưu mã giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi! Mã code có thể đã tồn tại.");
        }
        return "redirect:/admin/coupons";
    }

    // Xử lý xóa
    @GetMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            couponService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa mã giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa mã này.");
        }
        return "redirect:/admin/coupons";
    }
}