package com.cnpm.controller.admin;

import com.cnpm.entity.Category;
import com.cnpm.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Hiển thị danh sách danh mục
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        // Đối tượng rỗng để binding với form trong modal
        model.addAttribute("category", new Category()); 
        return "admin/categories";
    }

    // Xử lý lưu (thêm mới hoặc cập nhật)
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("category") Category category, RedirectAttributes redirectAttributes) {
        
        // **LOGIC SỬA LỖI HOÀN CHỈNH**

        // TRƯỜNG HỢP 1: CẬP NHẬT DANH MỤC CŨ
        if (category.getCategoryId() != null) {
            // Lấy đối tượng category cũ từ database
            Category existingCategory = categoryService.findById(category.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + category.getCategoryId()));
            
            // Chỉ cập nhật tên, giữ nguyên ảnh cũ
            existingCategory.setCategoryName(category.getCategoryName());
            categoryService.save(existingCategory);
            redirectAttributes.addFlashAttribute("success", "Cập nhật danh mục thành công!");

        // TRƯỜNG HỢP 2: TẠO MỚI DANH MỤC
        } else {
            // Gán ảnh mặc định nếu tạo mới mà không có ảnh
            if (category.getImageUrl() == null || category.getImageUrl().isEmpty()) {
                category.setImageUrl("/images/categories/default.png");
            }
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("success", "Thêm danh mục mới thành công!");
        }
        
        return "redirect:/admin/categories";
    }

    // Xử lý xóa
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục này vì có sản phẩm đang sử dụng.");
        }
        return "redirect:/admin/categories";
    }
}