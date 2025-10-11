using UnityEngine;
using TMPro;  // TextMeshPro için

public class GridManager : MonoBehaviour
{
    [Header("Grid Settings")]
    public GameObject grid_button;     // Prefab referansı
    public Transform contentParent;   // Grid Layout Group olan Content objesi
    public int gridWidth = 7;         // Grid genişliği
    public int gridHeight = 10;       // Grid yüksekliği
    
    [Header("Crossword Integration")]
    public CrosswordPuzzle crosswordPuzzle;
    
    private int totalCells;
    
    void Start()
    {
        // Crossword puzzle'ı bul
        if (crosswordPuzzle == null)
            crosswordPuzzle = FindObjectOfType<CrosswordPuzzle>();

        totalCells = gridWidth * gridHeight;
        GenerateGrid();
    }

    void GenerateGrid()
    {
        // Önce var olanları temizle
        foreach (Transform child in contentParent)
        {
            Destroy(child.gameObject);
        }

        // Hücreleri oluştur
        for (int i = 0; i < totalCells; i++)
        {
            int x = i % gridWidth;
            int y = i / gridWidth;
            
            GameObject newCell = Instantiate(grid_button, contentParent);
            newCell.name = $"Cell_{x}_{y}";

            // GridCell component'i ekle
            GridCell gridCell = newCell.GetComponent<GridCell>();
            if (gridCell == null)
            {
                gridCell = newCell.AddComponent<GridCell>();
            }

            // Crossword puzzle'dan bilgileri al
            if (crosswordPuzzle != null)
            {
                // Cell type'ına göre ayarla
                int cellType = crosswordPuzzle.GetCellType(x, y);
                gridCell.SetCellType(cellType);
                
                // Eğer letter cell ise, numarayı kontrol et
                if (cellType == 1)
                {
                    int number = crosswordPuzzle.GetNumber(x, y);
                    if (number > 0)
                    {
                        gridCell.SetNumber(number);
                    }
                }
                
                // Text component'i ayarla
                TextMeshProUGUI text = newCell.GetComponentInChildren<TextMeshProUGUI>();
                if (text != null && cellType == 1)
                {
                    text.text = ""; // Başlangıçta boş
                    text.fontSize = 24f;
                    text.color = Color.black;
                    text.alignment = TextAlignmentOptions.Center;
                    text.fontStyle = FontStyles.Bold;
                }
            }
        }
    }
    
    // Public metodlar - diğer scriptler için
    public void RefreshGrid()
    {
        GenerateGrid();
    }
    
    public int GetTotalCells()
    {
        return totalCells;
    }
    
    public Vector2 GetGridSize()
    {
        return new Vector2(gridWidth, gridHeight);
    }
}
