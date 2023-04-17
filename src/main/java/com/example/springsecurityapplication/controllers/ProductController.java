package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.repositories.ProductRepository;
import com.example.springsecurityapplication.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
// путь по умолчанию
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;

    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping("")
    public String getAllProduct(Model model) {
        model.addAttribute("products", productService.getAllProduct());
        return "/product/product";
    }

    @GetMapping("/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model) {
        model.addAttribute("product", productService.getProductId(id));
        return "/product/infoProduct";
    }

    @PostMapping("/search")
    public String productSearch(@RequestParam("search") String search, @RequestParam("from") String from, @RequestParam(
            "to") String to, @RequestParam(value = "price", required = false, defaultValue = "") String price,
                                @RequestParam(value = "category", required = false, defaultValue = "") String category,
                                Model model) {
        model.addAttribute("products", productService.getAllProduct());

        if (!from.isEmpty() & !to.isEmpty()) {
            if (!price.isEmpty()) {
                if (price.equals("sorted_by_ascending_price")) {
                    if (!category.isEmpty()) {
                        if (category.equals("traditional")) {
                            model.addAttribute("search_product",
                                    productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(from), Float.parseFloat(to), 4));
                        } else if (category.equals("sweet")) {
                            model.addAttribute("search_product",
                                    productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(from), Float.parseFloat(to), 5));
                        } else if (category.equals("tea")) {
                            model.addAttribute("search_product",
                                    productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(from), Float.parseFloat(to), 6));
                        }
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(from), Float.parseFloat(to)));
                    }
                } else if (price.equals("sorted_by_descending_price")) {
                    if (!category.isEmpty()) {
                        System.out.println(category);
                        if (category.equals("traditional")) {
                            model.addAttribute("search_product",
                                    productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(),
                                            Float.parseFloat(from), Float.parseFloat(to), 4));
                        } else if (category.equals("sweet")) {
                            model.addAttribute("search_product",
                                    productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(),
                                            Float.parseFloat(from), Float.parseFloat(to), 5));
                        } else if (category.equals("tea")) {
                            model.addAttribute("search_product",
                                    productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(),
                                            Float.parseFloat(from), Float.parseFloat(to), 6));
                        }
                    } else {
                        model.addAttribute("search_product",
                                productRepository.findByTitleOrderByPriceDesc(search.toLowerCase(),
                                        Float.parseFloat(from), Float.parseFloat(to)));
                    }
                }
            } else {
                model.addAttribute("search_product", productRepository.findByTitleAndPriceGreaterThanEqualAndPriceLessThanEqual(search.toLowerCase(), Float.parseFloat(from), Float.parseFloat(to)));
            }
        } else {
            model.addAttribute("search_product", productRepository.findByTitleContainingIgnoreCase(search));
        }

        model.addAttribute("value_search", search);
        model.addAttribute("value_price_from", from);
        model.addAttribute("value_price_to", to);
        return "/product/product";

    }
}
