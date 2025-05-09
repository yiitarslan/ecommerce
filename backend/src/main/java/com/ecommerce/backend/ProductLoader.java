package com.ecommerce.backend;

import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductLoader implements CommandLineRunner {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public ProductLoader(ProductRepository productRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public void run(String... args) {
        if (productRepo.count() == 0 && categoryRepo.count() == 0) {
            // 🔹 Kategorileri ekle
            Category men = categoryRepo.save(new Category(null, "men's clothing"));
            Category women = categoryRepo.save(new Category(null, "women's clothing"));
            Category electronics = categoryRepo.save(new Category(null, "electronics"));
            Category jewelery = categoryRepo.save(new Category(null, "jewelery"));

            // 🔹 Ürünleri ekle
            productRepo.save(new Product(null, "Mens Casual Premium Slim Fit T-Shirts", "Slim-fitting style, contrast raglan long sleeve...", men, "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 22.3));
            productRepo.save(new Product(null, "Mens Cotton Jacket", "Great outerwear jackets for Spring/Autumn...", men, "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg", 55.99));
            productRepo.save(new Product(null, "Mens Casual Slim Fit", "The color could be slightly different...", men, "https://fakestoreapi.com/img/71YXzeOuslL._AC_UY879_.jpg", 15.99));
            productRepo.save(new Product(null, "John Hardy Women's Legends Naga...", "From our Legends Collection, the Naga was inspired...", jewelery, "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_.jpg", 695.0));
            productRepo.save(new Product(null, "Solid Gold Petite Micropave", "Satisfaction Guaranteed. Return or exchange...", jewelery, "https://fakestoreapi.com/img/61sbMiUnoGL._AC_UL640_QL65_ML3_.jpg", 168.0));
            productRepo.save(new Product(null, "White Gold Plated Princess", "Classic Created Wedding Engagement...", jewelery, "https://fakestoreapi.com/img/71YAIFU48IL._AC_UL640_QL65_ML3_.jpg", 9.99));
            productRepo.save(new Product(null, "Pierced Owl Rose Gold Plated Stainless...", "Rose Gold Plated Double Flared Tunnel Plug...", jewelery, "https://fakestoreapi.com/img/51UDEzMJVpL._AC_UL640_QL65_ML3_.jpg", 10.99));
            productRepo.save(new Product(null, "WD 2TB Elements Portable External Hard Drive", "USB 3.0 and USB 2.0 Compatibility Fast data transfers...", electronics, "https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_.jpg", 64.0));
            productRepo.save(new Product(null, "SanDisk SSD PLUS 1TB Internal SSD", "Easy upgrade for faster boot up...", electronics, "https://fakestoreapi.com/img/61U7T1koQqL._AC_SX679_.jpg", 109.0));
            productRepo.save(new Product(null, "Silicon Power 256GB SSD 3D NAND", "3D NAND flash are applied to deliver high transfer speeds...", electronics, "https://fakestoreapi.com/img/71kWymZ+c+L._AC_SX679_.jpg", 109.0));
            productRepo.save(new Product(null, "WD 4TB Gaming Drive Works with Playstation 4", "Expand your PS4 gaming experience...", electronics, "https://fakestoreapi.com/img/61mtL65D4cL._AC_SX679_.jpg", 114.0));
            productRepo.save(new Product(null, "Acer SB220Q bi 21.5 inches Full HD Monitor", "21.5 inches Full HD (1920 x 1080) widescreen IPS display...", electronics, "https://fakestoreapi.com/img/81QpkIctqPL._AC_SX679_.jpg", 599.0));
            productRepo.save(new Product(null, "Samsung 49-Inch CHG90 144Hz Curved Monitor", "49 INCH SUPER ULTRAWIDE 32:9 CURVED GAMING MONITOR...", electronics, "https://fakestoreapi.com/img/81Zt42ioCgL._AC_SX679_.jpg", 999.99));
            productRepo.save(new Product(null, "BIYLACLESEN Women's 3-in-1 Snowboard Jacket", "Note:The Jackets is US standard size...", women, "https://fakestoreapi.com/img/51Y5NI-I5jL._AC_UX679_.jpg", 56.99));
            productRepo.save(new Product(null, "Lock and Love Women's Removable Hooded Jacket", "Faux leather material for style and comfort...", women, "https://fakestoreapi.com/img/81XH0e8fefL._AC_UY879_.jpg", 29.95));
            productRepo.save(new Product(null, "Rain Jacket Women Windbreaker", "Lightweight perfet for trip or casual wear...", women, "https://fakestoreapi.com/img/71HblAHs5xL._AC_UY879_-2.jpg", 39.99));
            productRepo.save(new Product(null, "MBJ Women's Solid Short Sleeve", "95% RAYON 5% SPANDEX, Lightweight fabric with great stretch...", women, "https://fakestoreapi.com/img/71z3kpMAYsL._AC_UY879_.jpg", 9.85));
            productRepo.save(new Product(null, "Opna Women's Short Sleeve Moisture", "100% Polyester, Machine wash, breathable with moisture wicking...", women, "https://fakestoreapi.com/img/51eg55uWmdL._AC_UX679_.jpg", 7.95));
            productRepo.save(new Product(null, "DANVOUY Womens T Shirt Casual Cotton Short", "95%Cotton,5%Spandex, Casual, Short Sleeve, Letter Print...", women, "https://fakestoreapi.com/img/61pHAEJ4NML._AC_UX679_.jpg", 12.99));

            System.out.println("🟢 Kategoriler ve ürünler başarıyla yüklendi.");
        }
    }
}
