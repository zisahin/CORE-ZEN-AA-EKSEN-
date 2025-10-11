using UnityEngine;
using UnityEngine.UI;
using TMPro;
using System.Collections.Generic;
using System.Globalization;

public class TouchInputManager : MonoBehaviour
{
    [Header("Components")]
    public CrosswordPuzzle crosswordPuzzle;
    public GridManager gridManager;
    
    [Header("Input Settings")]
    public KeyCode[] keyboardKeys = {
        KeyCode.A, KeyCode.B, KeyCode.C, KeyCode.D, KeyCode.E, KeyCode.F, KeyCode.G,
        KeyCode.H, KeyCode.I, KeyCode.J, KeyCode.K, KeyCode.L, KeyCode.M, KeyCode.N,
        KeyCode.O, KeyCode.P, KeyCode.Q, KeyCode.R, KeyCode.S, KeyCode.T, KeyCode.U,
        KeyCode.V, KeyCode.W, KeyCode.X, KeyCode.Y, KeyCode.Z
    };
    
    [Header("Visual Feedback")]
    public Color selectedCellColor = Color.yellow;
    public Color correctLetterColor = Color.green;
    public Color incorrectLetterColor = Color.red;
    public Color normalCellColor = Color.white;
    
    // Input state
    private GridCell selectedCell;
    private List<GridCell> gridCells = new List<GridCell>();
    
    // Events
    public System.Action<int, int, char> OnLetterEntered;
    public System.Action<int, int> OnCellSelected;
    
    void Start()
    {
        if (crosswordPuzzle == null)
            crosswordPuzzle = FindObjectOfType<CrosswordPuzzle>();
            
        if (gridManager == null)
            gridManager = FindObjectOfType<GridManager>();
            
        SetupInputSystem();
    }
    
    void Update()
    {
        HandleKeyboardInput();
        HandleTouchInput();
        HandleMobileKeyboard();
    }
    
    void SetupInputSystem()
    {
        // Grid celllerini topla ve setup yap
        StartCoroutine(SetupGridCellsDelayed());
    }
    
    System.Collections.IEnumerator SetupGridCellsDelayed()
    {
        // GridManager'ın grid oluşturmasını bekle
        yield return new WaitForEndOfFrame();
        
        CollectGridCells();
        SetupGridCellInteractions();
    }
    
    void CollectGridCells()
    {
        gridCells.Clear();
        
        // GridManager'ın contentParent'ından tüm child'ları al
        for (int i = 0; i < gridManager.contentParent.childCount; i++)
        {
            Transform child = gridManager.contentParent.GetChild(i);
            GridCell gridCell = child.GetComponent<GridCell>();
            
            if (gridCell == null)
            {
                gridCell = child.gameObject.AddComponent<GridCell>();
            }
            
            // Grid pozisyonunu hesapla (7 sütun)
            int x = i % 7;
            int y = i / 7;
            
            gridCell.Initialize(x, y, this);
            gridCells.Add(gridCell);
        }
    }
    
    void SetupGridCellInteractions()
    {
        foreach (GridCell cell in gridCells)
        {
            // Her cell'e Button component ekle
            Button button = cell.GetComponent<Button>();
            if (button == null)
            {
                button = cell.gameObject.AddComponent<Button>();
            }
            
            // Click event'i ekle
            int x = cell.gridX;
            int y = cell.gridY;
            button.onClick.RemoveAllListeners();
            button.onClick.AddListener(() => SelectCell(x, y));
            
            // Cell'in tipine göre görünümünü ayarla
            UpdateCellVisual(cell);
        }
    }
    
    void HandleKeyboardInput()
    {
        if (selectedCell == null) return;
        
        // Harf tuşlarını kontrol et (A-Z)
        for (int i = 0; i < keyboardKeys.Length; i++)
        {
            if (Input.GetKeyDown(keyboardKeys[i]))
            {
                char letter = (char)('A' + i);
                EnterLetter(selectedCell.gridX, selectedCell.gridY, letter);
                break;
            }
        }
        
        // Türkçe karakterler için özel kontrol
        if (Input.anyKeyDown)
        {
            string input = Input.inputString;
            if (!string.IsNullOrEmpty(input))
            {
                char inputChar = char.ToUpper(input[0], new CultureInfo("tr-TR"));
                // Türkçe karakterleri kontrol et
                if (inputChar == 'İ' || inputChar == 'Ğ' || inputChar == 'Ü' || 
                    inputChar == 'Ş' || inputChar == 'Ö' || inputChar == 'Ç')
                {
                    EnterLetter(selectedCell.gridX, selectedCell.gridY, inputChar);
                }
            }
        }
        
        // Silme tuşları
        if (Input.GetKeyDown(KeyCode.Backspace) || Input.GetKeyDown(KeyCode.Delete))
        {
            int currentRow = selectedCell.gridY;
            EnterLetter(selectedCell.gridX, selectedCell.gridY, ' ');
            
            // Eğer bu satır önceden tamamlanmışsa ve şimdi incomplete olduysa, neutral renk yap
            if (!IsRowCompleted(currentRow))
            {
                ResetRowToNeutral(currentRow);
            }
            
            // Backspace ile bir önceki hücreye git
            MoveToPreviousCell(selectedCell.gridX, selectedCell.gridY);
        }
    }
    
    void HandleTouchInput()
    {
        // Touch input'u Button component'leri üzerinden handle ediliyor
        // Bu fonksiyon gelecekte özel touch gesture'ları için kullanılabilir
        
        if (Input.touchCount > 0)
        {
            Touch touch = Input.GetTouch(0);
            
            if (touch.phase == TouchPhase.Began)
            {
                // Raycast ile hangi cell'e dokunulduğunu bul
                Vector2 touchPosition = Camera.main.ScreenToWorldPoint(touch.position);
                // Bu işlem Button.onClick events tarafından handle ediliyor
            }
        }
    }
    
    private TouchScreenKeyboard mobileKeyboard;
    
    private string lastKeyboardText = "";
    
    void HandleMobileKeyboard()
    {
        // Telefon klavyesi kontrolü - sadece gerçek mobil cihazlarda
        #if UNITY_ANDROID || UNITY_IOS
        if (!Application.isEditor) // Editor'da çalıştırma
        {
            try
            {
                if (mobileKeyboard != null && mobileKeyboard.active)
                {
                    string currentText = mobileKeyboard.text;
                    
                    // Backspace algılama - text kısaldıysa backspace basılmış demektir
                    if (currentText.Length < lastKeyboardText.Length && selectedCell != null)
                    {
                        // Backspace işlemi
                        int currentRow = selectedCell.gridY;
                        EnterLetter(selectedCell.gridX, selectedCell.gridY, ' ');
                        
                        // Eğer bu satır önceden tamamlanmışsa ve şimdi incomplete olduysa, neutral renk yap
                        if (!IsRowCompleted(currentRow))
                        {
                            ResetRowToNeutral(currentRow);
                        }
                        
                        // Backspace ile bir önceki hücreye git
                        MoveToPreviousCell(selectedCell.gridX, selectedCell.gridY);
                        
                        Debug.Log($"Mobile Backspace: Hücre silindi");
                    }
                    // Yeni harf girişi
                    else if (!string.IsNullOrEmpty(currentText) && currentText.Length > lastKeyboardText.Length && selectedCell != null)
                    {
                        char letter = currentText.ToUpper(new CultureInfo("tr-TR"))[currentText.Length - 1];
                        EnterLetter(selectedCell.gridX, selectedCell.gridY, letter);
                    }
                    
                    lastKeyboardText = currentText;
                    mobileKeyboard.text = currentText; // Text'i koru
                }
            }
            catch (System.Exception e)
            {
                Debug.LogWarning($"Mobile keyboard error: {e.Message}");
            }
        }
        #endif
    }
    
    public void OpenMobileKeyboard()
    {
        #if UNITY_ANDROID || UNITY_IOS
        if (!Application.isEditor) // Editor'da çalıştırma
        {
            try
            {
                if (mobileKeyboard == null || !mobileKeyboard.active)
                {
                    mobileKeyboard = TouchScreenKeyboard.Open("", TouchScreenKeyboardType.Default, false, false, false, false);
                    lastKeyboardText = ""; // Yeni klavye açıldığında text'i sıfırla
                }
            }
            catch (System.Exception e)
            {
                Debug.LogWarning($"Mobile keyboard open error: {e.Message}");
            }
        }
        #endif
    }
    
    public void SelectCell(int x, int y)
    {
        // Önceki seçimi temizle - YENİ SİSTEME GÖRE
        if (selectedCell != null)
        {
            // Önceki hücrenin satırının tamamlanmış olup olmadığını kontrol et
            bool prevRowCompleted = IsRowCompleted(selectedCell.gridY);
            
            TextMeshProUGUI prevTextComponent = selectedCell.GetComponentInChildren<TextMeshProUGUI>();
            bool prevHasLetter = prevTextComponent != null && !string.IsNullOrEmpty(prevTextComponent.text);
            
            if (prevHasLetter && prevRowCompleted)
            {
                // Satır tamamlanmış - doğru rengi koru
                char prevCurrentLetter = prevTextComponent.text[0];
                bool prevIsCorrect = crosswordPuzzle.IsCorrectLetter(selectedCell.gridX, selectedCell.gridY, prevCurrentLetter);
                UpdateCellVisual(selectedCell, false, prevIsCorrect, true);
            }
            else if (prevHasLetter)
            {
                // Satır henüz tamamlanmamış - neutral renk
                UpdateCellVisual(selectedCell, false, null, true);
            }
            else
            {
                // Harf yok - normal beyaz
                UpdateCellVisual(selectedCell, false, null, false);
            }
        }
        
        // Yeni cell'i seç
        GridCell cell = GetGridCell(x, y);
        if (cell != null && crosswordPuzzle.GetCellType(x, y) == 1) // Sadece letter cell'ler seçilebilir
        {
            selectedCell = cell;
            UpdateCellVisual(selectedCell, true); // Yeni seçili hücre
            
            // Mobil cihazda klavyeyi aç
            #if UNITY_ANDROID || UNITY_IOS
            OpenMobileKeyboard();
            #endif
            
            OnCellSelected?.Invoke(x, y);
            
            Debug.Log($"Cell selected: ({x}, {y})");
        }
    }
    
    public void EnterLetter(int x, int y, char letter)
    {
        if (crosswordPuzzle.GetCellType(x, y) != 1) return; // Sadece letter cell'lere harf girilebilir

        GridCell cell = GetGridCell(x, y);
        if (cell == null) return;

        // Harfi cell'e yerleştir
        TextMeshProUGUI textComponent = cell.GetComponentInChildren<TextMeshProUGUI>();
        if (textComponent != null)
        {
            textComponent.text = letter == ' ' ? "" : letter.ToString().ToUpper(new CultureInfo("tr-TR"));
        }

        OnLetterEntered?.Invoke(x, y, letter);

        Debug.Log($"Letter entered: {letter} at ({x}, {y})");

        // Otomatik bir sonraki hücreye geç (sadece harf girilirse, silme işleminde değil)
        if (letter != ' ')
        {
            // Harf girildikten sonra neutral renk (beyaz/normal)
            UpdateCellVisual(cell, false, null, true); // Harf var ama henüz doğruluk kontrolü yok
            MoveToNextCell(x, y);
        }
        else
        {
            // Silme işleminde - harf yoksa normal beyaz
            UpdateCellVisual(cell, true, null, false); // Seçili kalır, harf yok
        }
    }
    
    GridCell GetGridCell(int x, int y)
    {
        int index = y * 7 + x; // 7 sütun
        if (index >= 0 && index < gridCells.Count)
        {
            return gridCells[index];
        }
        return null;
    }
    
    void UpdateCellVisual(GridCell cell, bool isSelected = false, bool? isCorrect = null, bool hasLetter = false)
    {
        if (cell == null) return;
        
        Image cellImage = cell.GetComponent<Image>();
        if (cellImage == null) return;
        
        // Renk belirleme
        Color targetColor = normalCellColor;
        
        if (crosswordPuzzle.GetCellType(cell.gridX, cell.gridY) == 0)
        {
            // Boş hücre - görünmez yap
            targetColor = Color.clear;
            cellImage.enabled = false;
            return;
        }
        else
        {
            cellImage.enabled = true;
        }
        
        if (isSelected)
        {
            targetColor = selectedCellColor;
        }
        else if (hasLetter && isCorrect.HasValue)
        {
            targetColor = isCorrect.Value ? correctLetterColor : incorrectLetterColor;
        }
        
        cellImage.color = targetColor;
    }
    
    void MoveToNextCell(int currentX, int currentY)
    {
        // Bir sonraki hücreyi hesapla
        int nextX = currentX + 1;
        int nextY = currentY;
        
        // Satır sonu kontrolü
        if (nextX >= 7) // 7 sütun
        {
            nextX = 0;
            nextY = currentY + 1;
            
            // SATIR DEĞİŞTİ - Önceki satırı kontrol et
            CheckAndUpdateRowColors(currentY);
        }
        
        // Grid sonu kontrolü
        if (nextY >= 10) // 10 satır
        {
            // Son satır tamamlandı - son satırı da kontrol et
            CheckAndUpdateRowColors(9);
            
            // İlk hücreye dön
            nextX = 0;
            nextY = 0;
        }
        
        // Bir sonraki aktif hücreyi bul
        GridCell nextCell = FindNextActiveCell(nextX, nextY);
        if (nextCell != null)
        {
            SelectCell(nextCell.gridX, nextCell.gridY);
        }
    }
    
    GridCell FindNextActiveCell(int startX, int startY)
    {
        // Başlangıç pozisyonundan itibaren aktif hücre ara
        for (int y = startY; y < 10; y++) // 10 satır
        {
            int startCol = (y == startY) ? startX : 0; // İlk satırda startX'ten başla, diğerlerinde 0'dan
            
            for (int x = startCol; x < 7; x++) // 7 sütun
            {
                if (crosswordPuzzle.GetCellType(x, y) == 1) // Letter cell ise
                {
                    return GetGridCell(x, y);
                }
            }
        }
        
        // Eğer bulunamazsa baştan ara
        for (int y = 0; y <= startY; y++)
        {
            int endCol = (y == startY) ? startX - 1 : 6; // Son satırda startX'e kadar, diğerlerinde sona kadar
            
            for (int x = 0; x <= endCol; x++)
            {
                if (crosswordPuzzle.GetCellType(x, y) == 1) // Letter cell ise
                {
                    return GetGridCell(x, y);
                }
            }
        }
        
        return null; // Aktif hücre bulunamadı
    }
    
    void CheckAndUpdateRowColors(int rowY)
    {
        // Bu satırdaki tüm hücreleri kontrol et
        bool allLettersFilled = true;
        bool allLettersCorrect = true;
        
        for (int x = 0; x < 7; x++) // 7 sütun
        {
            if (crosswordPuzzle.GetCellType(x, rowY) == 1) // Letter cell ise
            {
                GridCell cell = GetGridCell(x, rowY);
                if (cell != null)
                {
                    TextMeshProUGUI textComponent = cell.GetComponentInChildren<TextMeshProUGUI>();
                    
                    if (textComponent == null || string.IsNullOrEmpty(textComponent.text))
                    {
                        // Boş hücre var - henüz satır tamamlanmamış
                        allLettersFilled = false;
                        break;
                    }
                    else
                    {
                        // Harf var, doğru mu kontrol et
                        char currentLetter = textComponent.text[0];
                        if (!crosswordPuzzle.IsCorrectLetter(x, rowY, currentLetter))
                        {
                            allLettersCorrect = false;
                        }
                    }
                }
            }
        }
        
        // Eğer tüm harfler dolduysa renk ver
        if (allLettersFilled)
        {
            Color rowColor = allLettersCorrect ? correctLetterColor : incorrectLetterColor;
            
            // Tüm satırı boyala
            for (int x = 0; x < 7; x++)
            {
                if (crosswordPuzzle.GetCellType(x, rowY) == 1)
                {
                    GridCell cell = GetGridCell(x, rowY);
                    if (cell != null)
                    {
                        UpdateCellVisual(cell, false, allLettersCorrect, true);
                    }
                }
            }
            
            Debug.Log($"Satır {rowY + 1} tamamlandı - {(allLettersCorrect ? "DOĞRU" : "YANLIŞ")}");
        }
    }
    
    bool IsRowCompleted(int rowY)
    {
        // Bu satırdaki tüm letter cell'lerin dolu olup olmadığını kontrol et
        for (int x = 0; x < 7; x++) // 7 sütun
        {
            if (crosswordPuzzle.GetCellType(x, rowY) == 1) // Letter cell ise
            {
                GridCell cell = GetGridCell(x, rowY);
                if (cell != null)
                {
                    TextMeshProUGUI textComponent = cell.GetComponentInChildren<TextMeshProUGUI>();
                    
                    if (textComponent == null || string.IsNullOrEmpty(textComponent.text))
                    {
                        return false; // Boş hücre var
                    }
                }
            }
        }
        return true; // Tüm hücreler dolu
    }
    
    void ResetRowToNeutral(int rowY)
    {
        // Bu satırdaki tüm hücreleri neutral renge çevir
        for (int x = 0; x < 7; x++) // 7 sütun
        {
            if (crosswordPuzzle.GetCellType(x, rowY) == 1) // Letter cell ise
            {
                GridCell cell = GetGridCell(x, rowY);
                if (cell != null)
                {
                    TextMeshProUGUI textComponent = cell.GetComponentInChildren<TextMeshProUGUI>();
                    bool hasLetter = textComponent != null && !string.IsNullOrEmpty(textComponent.text);
                    
                    // Neutral renk (beyaz/normal) - doğru/yanlış kontrolü yok
                    UpdateCellVisual(cell, false, null, hasLetter);
                }
            }
        }
        
        Debug.Log($"Satır {rowY + 1} neutral renge çevrildi");
    }
    
    void MoveToPreviousCell(int currentX, int currentY)
    {
        // Bir önceki hücreyi hesapla
        int prevX = currentX - 1;
        int prevY = currentY;
        
        // Satır başı kontrolü
        if (prevX < 0)
        {
            prevX = 6; // Son sütun
            prevY = currentY - 1;
        }
        
        // Grid başı kontrolü
        if (prevY < 0)
        {
            // İlk hücreye gelince son hücreye git
            prevX = 6;
            prevY = 9;
        }
        
        // Bir önceki aktif hücreyi bul
        GridCell prevCell = FindPreviousActiveCell(prevX, prevY);
        if (prevCell != null)
        {
            // SelectCell zaten mevcut hücrenin visual'ını doğru şekilde güncelleyecek
            SelectCell(prevCell.gridX, prevCell.gridY);
        }
    }
    
    GridCell FindPreviousActiveCell(int startX, int startY)
    {
        // Başlangıç pozisyonundan geriye doğru aktif hücre ara
        for (int y = startY; y >= 0; y--) // 10 satır geriye
        {
            int startCol = (y == startY) ? startX : 6; // İlk satırda startX'ten başla, diğerlerinde son sütundan
            
            for (int x = startCol; x >= 0; x--) // 7 sütun geriye
            {
                if (crosswordPuzzle.GetCellType(x, y) == 1) // Letter cell ise
                {
                    return GetGridCell(x, y);
                }
            }
        }
        
        // Eğer bulunamazsa sondan ara
        for (int y = 9; y >= startY; y--)
        {
            int endCol = (y == startY) ? startX + 1 : 6; // Son satırda startX'ten sonra, diğerlerinde sondan başla
            
            for (int x = 6; x >= endCol; x--)
            {
                if (crosswordPuzzle.GetCellType(x, y) == 1) // Letter cell ise
                {
                    return GetGridCell(x, y);
                }
            }
        }
        
        return null; // Aktif hücre bulunamadı
    }
    
    // Public metodlar - diğer scriptler tarafından kullanılabilir
    public void ClearSelection()
    {
        if (selectedCell != null)
        {
            UpdateCellVisual(selectedCell);
            selectedCell = null;
        }
    }
    
    public Vector2 GetSelectedCellPosition()
    {
        if (selectedCell != null)
        {
            return new Vector2(selectedCell.gridX, selectedCell.gridY);
        }
        return Vector2.one * -1;
    }
    
    public void SetCellColor(int x, int y, Color color)
    {
        GridCell cell = GetGridCell(x, y);
        if (cell != null)
        {
            Image cellImage = cell.GetComponent<Image>();
            if (cellImage != null)
            {
                cellImage.color = color;
            }
        }
    }
}
