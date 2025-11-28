package com.example.BizzBuy.service;

import com.example.BizzBuy.model.Product;
import com.example.BizzBuy.util.IdGenerator;
import com.example.BizzBuy.util.JsonFileManager;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//automatically creates an instance of this class..! :O
@Service
public class ProductService{
    //save the path in a string..!
    private static final String PRODUCTS_FILE = "products.json";
    private final JsonFileManager fileManager;

    //this is directly used by springboot to instantiate this service class..!
    public ProductService(JsonFileManager fileManager){
        this.fileManager = fileManager;
    }


    //helper functions for the other methods..!
    public List<Product> fetchProducts(){
        return new ArrayList<>(fileManager.readList(PRODUCTS_FILE, Product.class));
    }
    private void writeBack(List<Product> products){
        fileManager.writeList(PRODUCTS_FILE, products);
    }


    public Product createProduct(Product newProduct, Long sellerId){
        List<Product> products = fetchProducts();

        //turns out you can do it in a much cleaner way..! using the build thingy..!
        // Product product = new Product();
        // product.setId(IdGenerator.nextId(products));
        // product.setSellerId(sellerId);
        // product.setName(newProduct.getName());
        // product.setDescription(newProduct.getDescription());
        // product.setImages(newProduct.getImages());
        // product.setPrice(newProduct.getPrice());
        // product.setStockQuantity(newProduct.getStockQuantity());
        // product.setTags(newProduct.getTags());

        //check if the product already exists(i.e if te entire object is same.. 
        //not just the name or something).. if so then just update the quantity..!
        for (Product existingProduct : products) {
            // We check if "Business Keys" match. 
            boolean isSameProduct = 
                Objects.equals(existingProduct.getSellerId(), sellerId) &&
                Objects.equals(existingProduct.getName(), newProduct.getName()) &&
                Objects.equals(existingProduct.getDescription(), newProduct.getDescription()) &&
                Objects.equals(existingProduct.getPrice(), newProduct.getPrice()) &&
                Objects.equals(existingProduct.getImages(), newProduct.getImages()) &&
                Objects.equals(existingProduct.getTags(), newProduct.getTags());

            if (isSameProduct) {
                //just increase the qty..!
                existingProduct.setStockQuantity(existingProduct.getStockQuantity() + newProduct.getStockQuantity());

                writeBack(products);
                return existingProduct; 
            }
        }

        //cool way to do it..! (i.e building / instantiating a new object..!)
        Product product = Product.builder()
            .id(IdGenerator.nextId(products))
            .sellerId(sellerId)
            .name(newProduct.getName())
            .description(newProduct.getDescription())
            .images(newProduct.getImages())
            .price(newProduct.getPrice())
            .stockQuantity(newProduct.getStockQuantity())
            .tags(newProduct.getTags()).build();

        //add to the list..! and write back to the file..!
        products.add(product);
        writeBack(products);
        return product;
    }

    public Product getProduct(Long productId){
        List<Product> products = fetchProducts();

        for(Product p : products){
            if(p.getId().equals(productId)){
                return p;
            }
        }
        throw new RuntimeException("Product not found");
    }

    public List<Product> findBySeller(Long sellerId){
        List<Product> products = fetchProducts();
        List<Product> finalList = new ArrayList<>();

        for(Product p : products){
            //this is much safer than using the .equals() directly...!
            if(Objects.equals(p.getSellerId(), sellerId)){
                finalList.add(p);
            }
        }

        return finalList;
    }

    public List<Product> search(String keyword){
        //if theres no text in the keyword... i.e its empty, then show all the products..!
        if (!StringUtils.hasText(keyword))
            return fetchProducts();

        String query = keyword.toLowerCase();
        List<Product> products = fetchProducts();
        List<Product> finalList = new ArrayList<>();

        for(Product p : products){
            boolean flag = false;

            //check in the name, description and tags..!
            if(p.getName() != null && p.getName().toLowerCase().contains(query))
                flag = true;
            else if(p.getDescription() != null && p.getDescription().toLowerCase().contains(query))
                flag = true;
            else if(p.getTags() != null){
                List<String> tags = p.getTags();
                for(String tag : tags){
                    if(tag != null && tag.toLowerCase().contains(query)){
                        flag = true;
                        break;
                    }
                } 
            }

            if(flag)
                finalList.add(p);

        }

        return finalList;
    }

    public List<Product> filter(Double min, Double max, Long sellerId){
        List<Product> allProducts = fetchProducts();
        List<Product> result = new ArrayList<>();

        for(Product p : allProducts){
            //if the conditions fail,then check the next product..!
            if(min != null && p.getPrice() < min)
                continue; 

            if(max != null && p.getPrice() > max)
                continue;
            
            if(sellerId != null && !Objects.equals(p.getSellerId(), sellerId))
                continue;
            
            result.add(p);
        }
        return result;
    }

    public Product updateStock(Long productId, Integer stock, Long sellerId) {
        List<Product> products = fetchProducts();

        for(Product p : products){
            if(p.getId().equals(productId)) {
                if(!p.getSellerId().equals(sellerId))
                    throw new IllegalArgumentException("Only owner can update stock");

                //p is just a reference to the corresponding product object, so
                //just changing it directly is fine..!
                p.setStockQuantity(stock);
                writeBack(products);

                return p;
            }
        }
        
        //if no prodct if found with that id..!
        throw new IllegalArgumentException("Product not found");
    }

    public void decreaseStock(Long productId, int quantity) {
        if(productId == null)
            throw new IllegalArgumentException("Product ID cannot be null");

        if(quantity <= 0)
            throw new IllegalArgumentException("Quantity to decrease must be greater than 0");

        List<Product> products = fetchProducts();

        for (Product p : products){
            if (p.getId().equals(productId)){
                if (p.getStockQuantity() < quantity)
                    throw new IllegalArgumentException("Insufficient stock. Available: " + p.getStockQuantity());

                p.setStockQuantity(p.getStockQuantity() - quantity);
                writeBack(products);
                return;
            }
        }

        throw new IllegalArgumentException("Product not found");
    }
}
