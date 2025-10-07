using System.Collections.Generic;
using UnityEngine;

[System.Serializable]
public class CrosswordWord
{
    public string word;
    public string clue;
    public int startX, startY;
    public bool isHorizontal;
    public int number;
    
    public CrosswordWord(string word, string clue, int startX, int startY, bool isHorizontal, int number)
    {
        this.word = word.ToUpper();
        this.clue = clue;
        this.startX = startX;
        this.startY = startY;
        this.isHorizontal = isHorizontal;
        this.number = number;
    }
}

public class CrosswordPuzzle : MonoBehaviour
{
    [Header("Grid Settings")]
    public int gridWidth = 7;
    public int gridHeight = 10;
    
    [Header("Words and Clues")]
    private List<CrosswordWord> words = new List<CrosswordWord>();
    
    // Grid array - 0 = empty, 1 = letter cell, 2 = blocked
    public int[,] gridStructure;
    public char[,] gridLetters;
    public int[,] gridNumbers; // Kelime başlangıç numaraları
    
    void Awake()
    {
        InitializeGrid();
        CreateWordList();
        PlaceWords();
    }
    
    void InitializeGrid()
    {
        gridStructure = new int[gridWidth, gridHeight];
        gridLetters = new char[gridWidth, gridHeight];
        gridNumbers = new int[gridWidth, gridHeight];
        
        // Başlangıçta tüm hücreler boş
        for (int x = 0; x < gridWidth; x++)
        {
            for (int y = 0; y < gridHeight; y++)
            {
                gridStructure[x, y] = 0;
                gridLetters[x, y] = ' ';
                gridNumbers[x, y] = 0;
            }
        }
    }
    
    void CreateWordList()
    {
        // Türkçe kelimeler - Her satırda TAM 7 harfli kelimeler
        // Sadece YATAY kelimeler - 10 satır boyunca
        
        words.Add(new CrosswordWord("KITAPCI", "Kitap satan kişi", 0, 0, true, 1));          // 1. satır - 7 harf
        words.Add(new CrosswordWord("MASALAR", "Çok sayıda masa", 0, 1, true, 2));           // 2. satır - 7 harf
        words.Add(new CrosswordWord("KEDILER", "Miyavlayan hayvanlar", 0, 2, true, 3));      // 3. satır - 7 harf
        words.Add(new CrosswordWord("ARABASI", "Onun aracı", 0, 3, true, 4));                // 4. satır - 7 harf
        words.Add(new CrosswordWord("KALEMIM", "Benim yazı aletim", 0, 4, true, 5));         // 5. satır - 7 harf
        words.Add(new CrosswordWord("DENIZCI", "Denizde çalışan", 0, 5, true, 6));           // 6. satır - 7 harf
        words.Add(new CrosswordWord("GUNESLI", "Işık veren gün", 0, 6, true, 7));            // 7. satır - 7 harf
        words.Add(new CrosswordWord("AGACLAR", "Yapraklı bitkiler", 0, 7, true, 8));         // 8. satır - 7 harf
        words.Add(new CrosswordWord("EVIMDEN", "Yaşadığım yerden", 0, 8, true, 9));          // 9. satır - 7 harf
        words.Add(new CrosswordWord("DOSTLUK", "Arkadaşlık bağı", 0, 9, true, 10));          // 10. satır - 7 harf
    }
    
    void PlaceWords()
    {
        foreach (CrosswordWord word in words)
        {
            PlaceWord(word);
        }
    }
    
    bool PlaceWord(CrosswordWord word)
    {
        // Kelimenin yerleştirilebilir olup olmadığını kontrol et
        if (!CanPlaceWord(word))
        {
            Debug.LogWarning($"Kelime yerleştirilemedi: {word.word}");
            return false;
        }
        
        // Kelimeyi yerleştir
        for (int i = 0; i < word.word.Length; i++)
        {
            int x = word.isHorizontal ? word.startX + i : word.startX;
            int y = word.isHorizontal ? word.startY : word.startY + i;
            
            gridStructure[x, y] = 1; // Letter cell
            gridLetters[x, y] = word.word[i];
            
            // Kelime başlangıcına numara koy
            if (i == 0)
            {
                gridNumbers[x, y] = word.number;
            }
        }
        
        return true;
    }
    
    bool CanPlaceWord(CrosswordWord word)
    {
        // Grid sınırlarını kontrol et
        int endX = word.isHorizontal ? word.startX + word.word.Length - 1 : word.startX;
        int endY = word.isHorizontal ? word.startY : word.startY + word.word.Length - 1;
        
        if (endX >= gridWidth || endY >= gridHeight || word.startX < 0 || word.startY < 0)
        {
            return false;
        }
        
        // Her harf pozisyonunu kontrol et
        for (int i = 0; i < word.word.Length; i++)
        {
            int x = word.isHorizontal ? word.startX + i : word.startX;
            int y = word.isHorizontal ? word.startY : word.startY + i;
            
            // Eğer hücre dolu ise, aynı harf olmalı (çakışma için)
            if (gridStructure[x, y] == 1)
            {
                if (gridLetters[x, y] != word.word[i])
                {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public List<CrosswordWord> GetWords()
    {
        return words;
    }
    
    public char GetLetter(int x, int y)
    {
        if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight)
        {
            return gridLetters[x, y];
        }
        return ' ';
    }
    
    public int GetCellType(int x, int y)
    {
        if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight)
        {
            return gridStructure[x, y];
        }
        return 0;
    }
    
    public int GetNumber(int x, int y)
    {
        if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight)
        {
            return gridNumbers[x, y];
        }
        return 0;
    }
    
    public bool IsCorrectLetter(int x, int y, char letter)
    {
        return GetLetter(x, y) == char.ToUpper(letter);
    }
}
