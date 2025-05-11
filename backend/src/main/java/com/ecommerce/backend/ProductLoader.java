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

            productRepo.save(new Product(null, "Slim Fit Polo T-Shirt", "Soft cotton slim fit polo, perfect for summer.", men, "https://image.hm.com/assets/hm/1b/9a/1b9a40df036db9f0eeaf83ccda1a5c1445f1e93a.jpg?imwidth=564", 599.99));
            productRepo.save(new Product(null, "Casual Denim Jacket", "Blue denim jacket with button closure.", women, "https://image.hm.com/assets/hm/ee/e2/eee238f747baf160799fb41bbf0635452b57fd8b.jpg?imwidth=564", 1199.99));
            productRepo.save(new Product(null, "Basic White T-Shirt", "Essential white t-shirt with relaxed fit.", men, "https://image.hm.com/assets/hm/e4/af/e4af2eae840fddf244b30cb264ef4bf04e5c45b6.jpg?imwidth=564", 499.50));
            productRepo.save(new Product(null, "Elegant Pearl Necklace", "Genuine freshwater pearls with silver clasp.", jewelery, "https://media.tiffany.com/is/image/Tiffany/EcomItemL2/tiffany-essential-pearlsnecklace-33475543_1045864_AV_1.jpg?&op_usm=1.0,1.0,6.0&defaultImage=NoImageAvailableInternal&&defaultImage=NoImageAvailableInternal&fmt=webp", 4999.90));
            productRepo.save(new Product(null, "Crystal Stud Earrings", "Shiny zirconia studs with gold plating.", jewelery, "https://media.tiffany.com/is/image/Tiffany/EcomBrowseM/tiffany-victoriaearrings-38050982_1031018_ED.jpg?&defaultImage=NoImageAvailableInternal&&defaultImage=NoImageAvailableInternal&fmt=webp", 2099.99));
            productRepo.save(new Product(null, "Stainless Steel Bracelet", "Minimalist bracelet with magnetic clasp.", jewelery, "https://fossil.scene7.com/is/image/FossilPartners/JF04697040_onmodelmale?$sfcc_onmodel_large$", 1300.99));
            productRepo.save(new Product(null, "Bluetooth Wireless Earbuds", "Noise-cancelling true wireless earphones.", electronics, "https://productimages.hepsiburada.net/s/404/222-222/110000429711422.jpg/format:webp", 2999.00));
            productRepo.save(new Product(null, "Gaming Mouse RGB", "Ergonomic mouse with customizable RGB lights.", electronics, "https://cdn.akakce.com/x/steelseries/steelseries-rival-3-rgb-optik-kablolu-oyuncu-mouse.jpg", 179.75));
            productRepo.save(new Product(null, "Mechanical Keyboard", "Blue switch mechanical keyboard, full size.", electronics, "https://img.kwcdn.com/product/fancy/1169e2eb-5aa4-4ba4-862e-21809f136fc2.jpg?imageView2/2/w/800/q/70/format/webp",7000.00 ));
            productRepo.save(new Product(null, "High-Waist Yoga Leggings", "Stretchy leggings for active movement.", women, "https://image.hm.com/assets/hm/3d/77/3d778d442905a56bc696e012b38785285095cb89.jpg?imwidth=564", 799.99));
            productRepo.save(new Product(null, "Floral Summer Dress", "Light and flowy dress with floral print.", women, "https://image.hm.com/assets/hm/66/62/6662bd9013800add06657d15be2b601b975cb46f.jpg?imwidth=564", 640.50));
            productRepo.save(new Product(null, "Padded Winter Coat", "Thick coat with detachable hood and zip.", women, "https://image.hm.com/assets/hm/ee/1d/ee1dd4f27bf0df84430e8c7e6b5676bdb0f3456a.jpg?imwidth=564", 1100.99));
            productRepo.save(new Product(null, "Smart Fitness Watch", "Tracks heart rate, steps, and sleep.", electronics, "https://m.media-amazon.com/images/I/61rxpOKVuML._AC_UL320_.jpg", 6900.90));
            productRepo.save(new Product(null, "Gold Chain Necklace", "18K gold plated chain, classic design.", jewelery, "https://fossil.scene7.com/is/image/FossilPartners/JF04612710_onmodel?$sfcc_onmodel_large$", 2300.99));
            productRepo.save(new Product(null, "Graphic Print Hoodie", "Oversized hoodie with streetwear graphic.", men, "https://image.hm.com/assets/hm/36/cb/36cb1706ab581f1cc862de333eeb837113167376.jpg?imwidth=1260", 900.00));
            System.out.println("🟢 Kategoriler ve ürünler başarıyla yüklendi.");
        }
    }
}
