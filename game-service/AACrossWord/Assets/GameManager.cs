using UnityEngine;
using UnityEngine.UI;
using TMPro;
using System.Collections.Generic;
using System.Linq;

public class GameManager : MonoBehaviour
{
    [Header("Game Components")]
    public CrosswordPuzzle crosswordPuzzle;
    public TouchInputManager inputManager;
    public GridManager gridManager;
    
    [Header("UI Elements")]
    public TextMeshProUGUI scoreText;
    public TextMeshProUGUI timeText;
    public TextMeshProUGUI clueText;
    public Button hintButton;
    public Button resetButton;
    public GameObject winPanel;
    
    [Header("Game Settings")]
    public int hintCost = 10;
    
    [Header("Scoring")]
    public int correctLetterScore = 10;
    public int wordCompleteBonus = 50;
    
    // Game state
    private int currentScore = 0;
    private bool gameActive = false;
    private Dictionary<string, bool> completedWords = new Dictionary<string, bool>();
    private CrosswordWord currentSelectedWord = null;
    
    // Player progress
    private char[,] playerGrid;
    
    void Start()
    {
        InitializeGame();
        SetupUI();
        StartGame();
    }
    
    void Update()
    {
        if (gameActive)
        {
            CheckGameWin();
        }
    }
    
    void InitializeGame()
    {
        // Component referanslarını bul
        if (crosswordPuzzle == null)
            crosswordPuzzle = FindObjectOfType<CrosswordPuzzle>();
            
        if (inputManager == null)
            inputManager = FindObjectOfType<TouchInputManager>();
            
        if (gridManager == null)
            gridManager = FindObjectOfType<GridManager>();
        
        // Player grid'i initialize et
        playerGrid = new char[crosswordPuzzle.gridWidth, crosswordPuzzle.gridHeight];
        ClearPlayerGrid();
        
        // Input manager event'lerini bağla
        if (inputManager != null)
        {
            inputManager.OnLetterEntered += OnLetterEntered;
            inputManager.OnCellSelected += OnCellSelected;
        }
        
        // Completed words dictionary'sini initialize et
        foreach (CrosswordWord word in crosswordPuzzle.GetWords())
        {
            completedWords[word.word] = false;
        }
    }
    
    void SetupUI()
    {
        // Button event'lerini bağla
        if (hintButton != null)
        {
            hintButton.onClick.AddListener(UseHint);
        }
        
        if (resetButton != null)
        {
            resetButton.onClick.AddListener(ResetGame);
        }
        
        // Win panel'i gizle
        if (winPanel != null)
        {
            winPanel.SetActive(false);
        }
        
        UpdateScoreDisplay();
    }
    
    void StartGame()
    {
        gameActive = true;
        currentScore = 0;
        
        UpdateScoreDisplay();
        
        // İlk ipucunu göster
        ShowRandomClue();
        
        Debug.Log("Crossword oyunu başlatıldı!");
    }
    
    void OnLetterEntered(int x, int y, char letter)
    {
        // Player grid'i güncelle
        playerGrid[x, y] = letter;
        
        // Eğer harf doğruysa puan ver
        if (letter != ' ' && crosswordPuzzle.IsCorrectLetter(x, y, letter))
        {
            AddScore(correctLetterScore);
            CheckWordCompletion(x, y);
        }
        
        UpdateScoreDisplay();
    }
    
    void OnCellSelected(int x, int y)
    {
        // Seçilen cell'e göre ilgili kelimeyi bul ve ipucunu göster
        CrosswordWord word = FindWordAtPosition(x, y);
        if (word != null)
        {
            currentSelectedWord = word;
            ShowClue(word);
        }
    }
    
    void CheckWordCompletion(int x, int y)
    {
        // Bu pozisyondaki kelimeleri kontrol et
        List<CrosswordWord> wordsAtPosition = FindAllWordsAtPosition(x, y);
        
        foreach (CrosswordWord word in wordsAtPosition)
        {
            if (!completedWords[word.word] && IsWordComplete(word))
            {
                completedWords[word.word] = true;
                AddScore(wordCompleteBonus);
                
                // Visual feedback
                HighlightCompletedWord(word);
                
                Debug.Log($"Kelime tamamlandı: {word.word}");
            }
        }
    }
    
    bool IsWordComplete(CrosswordWord word)
    {
        for (int i = 0; i < word.word.Length; i++)
        {
            int x = word.isHorizontal ? word.startX + i : word.startX;
            int y = word.isHorizontal ? word.startY : word.startY + i;
            
            if (playerGrid[x, y] != word.word[i])
            {
                return false;
            }
        }
        return true;
    }
    
    void HighlightCompletedWord(CrosswordWord word)
    {
        Color completedColor = Color.green;
        
        for (int i = 0; i < word.word.Length; i++)
        {
            int x = word.isHorizontal ? word.startX + i : word.startX;
            int y = word.isHorizontal ? word.startY : word.startY + i;
            
            if (inputManager != null)
            {
                inputManager.SetCellColor(x, y, completedColor);
            }
        }
    }
    
    CrosswordWord FindWordAtPosition(int x, int y)
    {
        foreach (CrosswordWord word in crosswordPuzzle.GetWords())
        {
            // Pozisyonun bu kelime içinde olup olmadığını kontrol et
            bool inHorizontalWord = word.isHorizontal && 
                                   y == word.startY && 
                                   x >= word.startX && 
                                   x < word.startX + word.word.Length;
                                   
            bool inVerticalWord = !word.isHorizontal && 
                                 x == word.startX && 
                                 y >= word.startY && 
                                 y < word.startY + word.word.Length;
            
            if (inHorizontalWord || inVerticalWord)
            {
                return word;
            }
        }
        return null;
    }
    
    List<CrosswordWord> FindAllWordsAtPosition(int x, int y)
    {
        List<CrosswordWord> words = new List<CrosswordWord>();
        
        foreach (CrosswordWord word in crosswordPuzzle.GetWords())
        {
            bool inHorizontalWord = word.isHorizontal && 
                                   y == word.startY && 
                                   x >= word.startX && 
                                   x < word.startX + word.word.Length;
                                   
            bool inVerticalWord = !word.isHorizontal && 
                                 x == word.startX && 
                                 y >= word.startY && 
                                 y < word.startY + word.word.Length;
            
            if (inHorizontalWord || inVerticalWord)
            {
                words.Add(word);
            }
        }
        
        return words;
    }
    
    void ShowClue(CrosswordWord word)
    {
        if (clueText != null)
        {
            // Only show number and clue; all puzzles are horizontal
            clueText.text = $"{word.number}: {word.clue}";
        }
    }
    
    void ShowRandomClue()
    {
        List<CrosswordWord> incompleteWords = crosswordPuzzle.GetWords()
            .Where(w => !completedWords[w.word])
            .ToList();
            
        if (incompleteWords.Count > 0)
        {
            int randomIndex = Random.Range(0, incompleteWords.Count);
            ShowClue(incompleteWords[randomIndex]);
        }
    }
    
    void UseHint()
    {
        if (currentScore < hintCost)
        {
            Debug.Log("Yeterli puan yok!");
            return;
        }
        
        if (currentSelectedWord == null)
        {
            Debug.Log("Önce bir kelime seçin!");
            return;
        }
        
        // İlk boş harfi bul ve doldur
        for (int i = 0; i < currentSelectedWord.word.Length; i++)
        {
            int x = currentSelectedWord.isHorizontal ? currentSelectedWord.startX + i : currentSelectedWord.startX;
            int y = currentSelectedWord.isHorizontal ? currentSelectedWord.startY : currentSelectedWord.startY + i;
            
            if (playerGrid[x, y] != currentSelectedWord.word[i])
            {
                // İpucu ver
                char hintLetter = currentSelectedWord.word[i];
                inputManager.EnterLetter(x, y, hintLetter);
                
                // Puan düş
                AddScore(-hintCost);
                UpdateScoreDisplay();
                
                Debug.Log($"İpucu: {hintLetter}");
                break;
            }
        }
    }
    
    void ResetGame()
    {
        // Player grid'i temizle
        ClearPlayerGrid();
        
        // UI'ı reset et
        for (int x = 0; x < crosswordPuzzle.gridWidth; x++)
        {
            for (int y = 0; y < crosswordPuzzle.gridHeight; y++)
            {
                if (crosswordPuzzle.GetCellType(x, y) == 1)
                {
                    inputManager.EnterLetter(x, y, ' ');
                    inputManager.SetCellColor(x, y, Color.white);
                }
            }
        }
        
        // Game state'i reset et
        currentScore = 0;
        
        foreach (string word in completedWords.Keys.ToList())
        {
            completedWords[word] = false;
        }
        
        UpdateScoreDisplay();
        
        if (winPanel != null)
        {
            winPanel.SetActive(false);
        }
        
        gameActive = true;
        
        Debug.Log("Oyun sıfırlandı!");
    }
    
    void ClearPlayerGrid()
    {
        for (int x = 0; x < crosswordPuzzle.gridWidth; x++)
        {
            for (int y = 0; y < crosswordPuzzle.gridHeight; y++)
            {
                playerGrid[x, y] = ' ';
            }
        }
    }
    
    
    void CheckGameWin()
    {
        bool allWordsComplete = completedWords.Values.All(completed => completed);
        
        if (allWordsComplete)
        {
            gameActive = false;
            
            ShowWinScreen();
            
            Debug.Log("Tebrikler! Tüm kelimeleri tamamladınız!");
        }
    }
    
    void ShowWinScreen()
    {
        if (winPanel != null)
        {
            winPanel.SetActive(true);
        }
    }
    
    void AddScore(int points)
    {
        currentScore = Mathf.Max(0, currentScore + points);
    }
    
    void UpdateScoreDisplay()
    {
        if (scoreText != null)
        {
            scoreText.text = $"Puan: {currentScore}";
        }
    }
    
    
    // Public metodlar
    public int GetCurrentScore()
    {
        return currentScore;
    }
    
    
    public bool IsGameActive()
    {
        return gameActive;
    }
    
    public void PauseGame()
    {
        gameActive = false;
    }
    
    public void ResumeGame()
    {
        gameActive = true;
    }
}
