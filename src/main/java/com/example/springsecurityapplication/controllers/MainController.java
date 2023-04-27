package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.enumm.Status;
import com.example.springsecurityapplication.models.Cart;
import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.CartRepository;
import com.example.springsecurityapplication.repositories.OrderRepository;
import com.example.springsecurityapplication.repositories.ProductRepository;
import com.example.springsecurityapplication.security.PersonDetails;
import com.example.springsecurityapplication.services.PersonService;
import com.example.springsecurityapplication.services.ProductService;
import com.example.springsecurityapplication.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class MainController {

    private final ProductRepository productRepository;
    private final PersonValidator personValidator;
    private final PersonService personService;
    private final ProductService productService;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public MainController(ProductRepository productRepository, PersonValidator personValidator, PersonService personService, ProductService productService, CartRepository cartRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.personValidator = personValidator;
        this.personService = personService;
        this.productService = productService;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/personalaccount")
    public String index(Model model) {
        // получаем объект аутентификации - с помощью SecurityContextHolder обращаемся к контексту и на нём вызываем метод аутентификации. из сессии текущего пользователя получаем объект, который был положен в данную сессию после аутентифкации пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        String role = personDetails.getPerson().getRole();
        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin";
        }
        model.addAttribute("products", productService.getAllProduct());
        return "/user/index";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person) {
        return "registration";
    }

    @PostMapping("/registration")
    public String resultRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        personService.register(person);
        return "redirect:/personalaccount";
    }

    @GetMapping("/personalaccount/product")
    public String getAllProduct(Model model) {
        model.addAttribute("products", productService.getAllProduct());
        return "/user/index";
    }

    @GetMapping("/personalaccount/product/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model) {
        model.addAttribute("product", productService.getProductId(id));
        return "/user/infoProduct";
    }

    @PostMapping("/personalaccount/product/search")
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

    @GetMapping("/cart/add/{id}")
    public String addProductInCart(@PathVariable("id") int id, Model model) {
        // получаем продукт по id
        Product product = productService.getProductId(id);
        // извлекаем объект аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        // извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();
        Cart cart = new Cart(id_person, product.getId());
        cartRepository.save(cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        // извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();

        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();

        // получаем продукты из корзины по id товара
        for (Cart cart : cartList) {
            productList.add(productService.getProductId(cart.getProductId()));
        }

        // считаем итоговую цену
        float price = 0;
        for (Product product : productList) {
            price += product.getPrice();
        }

        model.addAttribute("price", price);
        model.addAttribute("cart_product", productList);
        return "/user/cart";
    }

    @GetMapping("/cart/delete/{id}")
    public String deleteProductFromCart(Model model, @PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        // извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();

        // получаем продукты из корзины по id товара
        for (Cart cart : cartList) {
            productList.add(productService.getProductId(cart.getProductId()));
        }
        cartRepository.deleteCartByProductId(id);
        return "redirect:/cart";
    }

    @GetMapping("/order/create")
    public String order() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        // извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();

        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();

        // получаем продукты из корзины по id товара
        for (Cart cart : cartList) {
            productList.add(productService.getProductId(cart.getProductId()));
        }

        // считаем итоговую цену
        float price = 0;
        for (Product product : productList) {
            price += product.getPrice();
        }

        String uuid = UUID.randomUUID().toString();
        for (Product product : productList) {
            Order newOrder = new Order(uuid, product, personDetails.getPerson(), 1, product.getPrice(), Status.Оформлен);
            orderRepository.save(newOrder);
            cartRepository.deleteCartByProductId(product.getId());
        }
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());
        model.addAttribute("orders", orderList);
        return "/user/orders";
    }

}
