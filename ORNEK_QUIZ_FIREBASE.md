# Örnek Quiz Verisi - Firebase'e Eklemek İçin

Firebase Console > Firestore Database > `quiz_questions` koleksiyonuna aşağıdaki verileri ekleyebilirsiniz.

## Örnek Quiz Sorusu 1 (Kolay)

```json
{
  "category": "Tarım",
  "correctAnswer": 2,
  "difficulty": "easy",
  "explanation": "Bursa Büyükşehir Belediyesi organik tarımı teşvik etmek için yeni bir proje başlattı.",
  "options": [
    "Ankara",
    "İzmir", 
    "Bursa",
    "Antalya"
  ],
  "points": 10,
  "question": "Organik Tarım Projesi hangi şehirde başlatıldı?",
  "sourceNewsTitle": "Bursa'da Organik Tarım Projesi"
}
```

## Örnek Quiz Sorusu 2 (Orta)

```json
{
  "category": "Siyaset",
  "correctAnswer": 1,
  "difficulty": "medium",
  "explanation": "CHP Genel Başkanı Özgür Özel, parti içi reformlar konusunda açıklamalarda bulundu.",
  "options": [
    "Kemal Kılıçdaroğlu",
    "Özgür Özel",
    "Ekrem İmamoğlu",
    "Mansur Yavaş"
  ],
  "points": 20,
  "question": "CHP'nin yeni genel başkanı kimdir?",
  "sourceNewsTitle": "Özgür Özel'in CHP Genel Başkanlığı"
}
```

## Örnek Quiz Sorusu 3 (Zor)

```json
{
  "category": "Ekonomi",
  "correctAnswer": 3,
  "difficulty": "hard",
  "explanation": "2024 yerel seçimleri Mart ayında gerçekleştirildi.",
  "options": [
    "Ocak 2024",
    "Şubat 2024",
    "Mart 2024",
    "Nisan 2024"
  ],
  "points": 30,
  "question": "2024 Yerel Seçimleri hangi ayda yapıldı?",
  "sourceNewsTitle": "2024 Yerel Seçimleri"
}
```

## Örnek Quiz Sorusu 4 (Çok Zor)

```json
{
  "category": "Genel",
  "correctAnswer": 0,
  "difficulty": "very_hard",
  "explanation": "Türkiye'de yapay zeka yatırımları son dönemde %150 artış gösterdi.",
  "options": [
    "%150",
    "%100",
    "%200",
    "%125"
  ],
  "points": 50,
  "question": "Türkiye'de yapay zeka yatırımları ne kadar artış gösterdi?",
  "sourceNewsTitle": "Türkiye'de Yapay Zeka Yatırımları"
}
```

## Habere Özel Quiz Eklemek İçin

Eğer belirli bir habere quiz eklemek istiyorsanız, `newsId` alanını da ekleyin:

```json
{
  "newsId": "HABER_ID_BURAYA",  // Firebase'deki haber ID'si
  "category": "Tarım",
  "correctAnswer": 2,
  "difficulty": "medium",
  "explanation": "...",
  "options": ["A", "B", "C", "D"],
  "points": 20,
  "question": "Soru metni",
  "sourceNewsTitle": "Haber başlığı"
}
```

## Hızlı Test İçin

Firebase Console'da:
1. `quiz_questions` koleksiyonuna git
2. "Add document" butonuna tıkla
3. "Auto ID" ile ID oluştur
4. Yukarıdaki JSON verilerinden birini kopyala
5. Her alanı manuel olarak ekle
6. "Save" ile kaydet

Alternatif olarak, Firebase Console'da "Import data" özelliğini kullanarak toplu ekleyebilirsiniz.

