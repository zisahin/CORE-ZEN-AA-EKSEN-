using UnityEngine;
using UnityEngine.UI;
using TMPro;
using System.Globalization;

public class GridCell : MonoBehaviour
{
    [Header("Grid Position")]
    public int gridX;
    public int gridY;
    
    [Header("Cell State")]
    public bool hasLetter = false;
    public char currentLetter = ' ';
    public bool isCorrect = false;
    
    [Header("Components")]
    private TextMeshProUGUI textComponent;
    private Image backgroundImage;
    private Button buttonComponent;
    
    [Header("Visual Settings")]
    public float animationDuration = 0.2f;
    
    private TouchInputManager inputManager;
    
    public void Initialize(int x, int y, TouchInputManager manager)
    {
        gridX = x;
        gridY = y;
        inputManager = manager;
        
        // Component referanslarını al
        textComponent = GetComponentInChildren<TextMeshProUGUI>();
        backgroundImage = GetComponent<Image>();
        buttonComponent = GetComponent<Button>();
        
        // Başlangıç durumunu ayarla
        SetupInitialState();
    }
    
    void SetupInitialState()
    {
        // Text component'i ayarla
        if (textComponent != null)
        {
            textComponent.text = "";
            textComponent.fontSize = 24f;
            textComponent.color = Color.black;
            textComponent.alignment = TextAlignmentOptions.Center;
            textComponent.fontStyle = FontStyles.Bold;
        }
        
        // Button component'i ayarla
        if (buttonComponent != null)
        {
            // Navigation'ı kapat (mobile için gereksiz)
            Navigation nav = buttonComponent.navigation;
            nav.mode = Navigation.Mode.None;
            buttonComponent.navigation = nav;
            
            // Button transition ayarları
            buttonComponent.transition = Selectable.Transition.ColorTint;
            ColorBlock colors = buttonComponent.colors;
            colors.normalColor = Color.white;
            colors.highlightedColor = Color.yellow;
            colors.pressedColor = Color.gray;
            colors.selectedColor = Color.yellow;
            colors.colorMultiplier = 1f;
            colors.fadeDuration = 0.1f;
            buttonComponent.colors = colors;
        }
    }
    
    public void SetLetter(char letter, bool animate = true)
    {
        currentLetter = letter;
        hasLetter = letter != ' ';
        
        if (textComponent != null)
        {
            string displayText = hasLetter ? letter.ToString().ToUpper(new CultureInfo("tr-TR")) : "";
            
            if (animate)
            {
                // Basit scale animasyonu
                StartCoroutine(AnimateLetterEntry(displayText));
            }
            else
            {
                textComponent.text = displayText;
            }
        }
    }
    
    System.Collections.IEnumerator AnimateLetterEntry(string text)
    {
        if (textComponent == null) yield break;
        
        // Scale down
        Vector3 originalScale = textComponent.transform.localScale;
        textComponent.transform.localScale = Vector3.zero;
        textComponent.text = text;
        
        // Scale up animation
        float elapsed = 0f;
        while (elapsed < animationDuration)
        {
            elapsed += Time.deltaTime;
            float t = elapsed / animationDuration;
            textComponent.transform.localScale = Vector3.Lerp(Vector3.zero, originalScale, t);
            yield return null;
        }
        
        textComponent.transform.localScale = originalScale;
    }
    
    public void SetCorrect(bool correct)
    {
        isCorrect = correct;
        
        // Renk değişikliği
        if (textComponent != null)
        {
            textComponent.color = correct ? Color.green : Color.red;
        }
    }
    
    public void SetHighlight(bool highlighted)
    {
        if (backgroundImage != null)
        {
            Color targetColor = highlighted ? Color.yellow : Color.white;
            backgroundImage.color = targetColor;
        }
    }
    
    public void SetCellType(int cellType)
    {
        // cellType: 0 = empty, 1 = letter, 2 = blocked
        
        bool isActive = cellType == 1;
        
        // Interactable durumunu ayarla
        if (buttonComponent != null)
        {
            buttonComponent.interactable = isActive;
        }
        
        // Görünürlüğü ayarla
        if (backgroundImage != null)
        {
            backgroundImage.enabled = isActive;
        }
        
        if (textComponent != null)
        {
            textComponent.enabled = isActive;
        }
        
        // Eğer empty cell ise, tüm componenti pasif yap
        gameObject.SetActive(cellType != 0);
    }
    
    public void SetNumber(int number)
    {
        // Kelime başlangıç numarası için küçük bir text oluştur
        if (number > 0)
        {
            GameObject numberObj = new GameObject("Number");
            numberObj.transform.SetParent(transform);
            
            TextMeshProUGUI numberText = numberObj.AddComponent<TextMeshProUGUI>();
            numberText.text = number.ToString();
            numberText.fontSize = 12f;
            numberText.color = Color.blue;
            numberText.fontStyle = FontStyles.Bold;
            
            // Pozisyonu sol üst köşeye ayarla
            RectTransform numberRect = numberObj.GetComponent<RectTransform>();
            numberRect.anchorMin = new Vector2(0, 1);
            numberRect.anchorMax = new Vector2(0, 1);
            numberRect.anchoredPosition = new Vector2(5, -5);
            numberRect.sizeDelta = new Vector2(20, 20);
        }
    }
    
    public char GetCurrentLetter()
    {
        return currentLetter;
    }
    
    public bool HasLetter()
    {
        return hasLetter;
    }
    
    public bool IsCorrect()
    {
        return isCorrect;
    }
    
    public void ClearCell()
    {
        SetLetter(' ', true);
        SetCorrect(false);
        SetHighlight(false);
    }
    
    // Touch ve Mouse events
    public void OnPointerEnter()
    {
        // Hover effect (opsiyonel)
        if (buttonComponent != null && buttonComponent.interactable)
        {
            SetHighlight(true);
        }
    }
    
    public void OnPointerExit()
    {
        // Hover effect (opsiyonel)
        if (inputManager != null)
        {
            Vector2 selectedPos = inputManager.GetSelectedCellPosition();
            bool isSelected = (selectedPos.x == gridX && selectedPos.y == gridY);
            
            if (!isSelected)
            {
                SetHighlight(false);
            }
        }
    }
}

