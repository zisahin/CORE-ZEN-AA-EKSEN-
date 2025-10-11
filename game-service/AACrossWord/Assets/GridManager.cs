using UnityEngine;
using TMPro;  // TextMeshPro için

public class GridManager : MonoBehaviour
{
    public GameObject grid_button;     // Prefab referansı
    public Transform contentParent;   // Grid Layout Group olan Content objesi

    public int totalCells = 35;       // Toplam hücre sayısı (örnek)
    
    void Start()
    {
        GenerateGrid(totalCells);
    }

    void GenerateGrid(int cellCount)
    {
        // Önce var olanları temizle
        foreach (Transform child in contentParent)
        {
            Destroy(child.gameObject);
        }

        // Hücreleri oluştur
        for (int i = 0; i < cellCount; i++)
        {
            GameObject newCell = Instantiate(grid_button, contentParent);

            // Hücreye örnek harf atama
            TextMeshProUGUI text = newCell.GetComponentInChildren<TextMeshProUGUI>();
            text.text = ((char)('A' + (i % 26))).ToString(); // A, B, C... döner
        }
    }
}
