🛒 Full-Stack E-Commerce Platform
Bu proje, Angular ve Spring Boot kullanılarak geliştirilmiş, ölçeklenebilir ve modüler yapıya sahip bir tam kapsamlı e-ticaret platformudur. Kullanıcı, satıcı ve yönetici rollerini destekleyen sistem; modern web teknolojileriyle responsive arayüz, güvenli kimlik doğrulama, dinamik ürün yönetimi ve entegre ödeme çözümleri sunar.

🚀 Temel Özellikler
Kimlik Doğrulama ve Yetkilendirme (JWT tabanlı)

Ürün Kataloğu: Listeleme, detay görüntüleme, filtreleme

Sepet ve Sipariş Yönetimi

Favori Ürünler Listesi

Stripe Entegrasyonu ile online ödeme alma

Yönetici Paneli: Kullanıcı, ürün ve sipariş denetimi

Satıcı Paneli: Ürün ekleme, güncelleme ve silme

Grafik Tabanlı Yönetim Paneli (Sipariş, gelir, kullanıcı analitiği)

Modüler Mimaride Lazy Loading Desteği

🧰 Teknoloji Yığını
🔹 Frontend
Angular 17

SCSS & Responsive Tasarım

Component & Module Tabanlı Yapı

Lazy Loading & Route Guard kullanımı

🔹 Backend
Spring Boot 3.x

Spring Security & JWT Authentication

Hibernate / JPA & MySQL

RESTful API mimarisi

Stripe SDK ile ödeme entegrasyonu

⚙️ Kurulum
Backend (Spring Boot)

cd backend

./mvnw spring-boot:run

application.properties dosyasında veritabanı bağlantı bilgilerinizi güncellemeyi unutmayın.

Frontend (Angular)

cd frontend

npm install

ng serve

Uygulama http://localhost:4200 adresinde çalışacaktır.

