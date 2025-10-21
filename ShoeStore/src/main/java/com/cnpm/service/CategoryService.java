package com.cnpm.service;

import com.cnpm.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> findAll(); 
    
    Optional<Category> findById(Integer id);
    Category save(Category category);
    void deleteById(Integer id);
}