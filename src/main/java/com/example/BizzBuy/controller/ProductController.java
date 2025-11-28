package com.example.BizzBuy.controller;

import com.example.BizzBuy.model.Product;
import com.example.BizzBuy.model.User;
import com.example.BizzBuy.service.ProductService;
import com.example.BizzBuy.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//specifies that this class is responsible for handling http requests..!
@RestController
//base part of the route.. or more like subnets in an block of ip addresses..!
@RequestMapping("/api/items")
//creates the constructor..!
@RequiredArgsConstructor


public class ProductController {
    private final ProductService productService;
    private final UserService userService;

    //mapping for the endpoint: (POST, {{base_url}}/api/items/products)
    @PostMapping("/products")
    // "?" cuz we dont know what we are gonna return..!
    //the annotations seem to be an easy way of reading the header and body
    // of the http request into the variables we need..!
    public ResponseEntity<?> createProduct(@RequestHeader("X-USER") String username, @RequestBody Product product){
        try{
            //get the userId from the username from the userService.
            User current = userService.requireByUsername(username);
            Product created = productService.createProduct(product, current.getId());
            return ResponseEntity.ok(created);
        }
        //errors from the user side..!
        catch(IllegalArgumentException | IllegalStateException ex){
            //map cuz its the closest to JSON we got in java..! json cuz its cleaner than plain text..!
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
        //error from our side..! (serverside error..!)
        catch(Exception ex){
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //mapping for the endpoint: (GET, {{base_url}}/api/items)
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try{
            List<Product> products = productService.fetchProducts();
            return ResponseEntity.ok(products);
        }
        catch(IllegalArgumentException | IllegalStateException ex){
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
        catch(Exception ex){
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //mapping for the endpoint: (GET, {{base_url}}/api/items/search)
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam(required = false) String keyword){
        try{
            List<Product> products = productService.search(keyword);
            return ResponseEntity.ok(products);
        }
        catch(IllegalArgumentException | IllegalStateException ex){
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
        catch(Exception ex){
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //mapping for the endpoint: (GET, {{base_url}}/api/items/filter)
    @GetMapping("/filter")
    public ResponseEntity<?> filterProducts(@RequestParam(required = false) Double min, @RequestParam(required = false) Double max, @RequestParam(required = false) Long sellerId){
        try{
            List<Product> products = productService.filter(min, max, sellerId);
            return ResponseEntity.ok(products);
        }
        catch (IllegalArgumentException | IllegalStateException ex){
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
        catch (Exception ex){
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //mapping for the endpoint: (GET, {{base_url}}/api/items/{{product_id}})
    @GetMapping("/{id}")
    public ResponseEntity<?> find(@PathVariable Long id){
        try{
            Product product = productService.getProduct(id);
            return ResponseEntity.ok(product);
        }
        catch (IllegalArgumentException | IllegalStateException ex){
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
        catch (Exception ex){
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //mapping for the endpoint: (GET, {{base_url}}/api/items/{{seller_id}}/{{product_id}})
    //###############################oke nvm, no longer in use..!########################3
    // @GetMapping("/seller/{id}")
    // public ResponseEntity<?> bySeller(@PathVariable Long id){
    //     try{
    //         List<Product> products = productService.findBySeller(id);
    //         return ResponseEntity.ok(products);
    //     }
    //     catch(IllegalArgumentException | IllegalStateException ex){
    //         Map<String, String> error = new HashMap<>();
    //         error.put("error", ex.getMessage());
    //         return ResponseEntity.badRequest().body(error);
    //     }
    //     catch(Exception ex){
    //         Map<String, String> error = new HashMap<>();
    //         error.put("error", ex.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    //     }
    // }

    //mapping for the endpoint: (PUT, {{base_url}}/api/items/{{product_id}}/stock?stock={{number}})
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@RequestHeader("X-USER") String username, @PathVariable Long id, @RequestParam Integer stock){
        try{
            User current = userService.requireByUsername(username);
            Product product = productService.updateStock(id, stock, current.getId());
            return ResponseEntity.ok(product);
        }
        catch(IllegalArgumentException | IllegalStateException ex){
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
        catch(Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
