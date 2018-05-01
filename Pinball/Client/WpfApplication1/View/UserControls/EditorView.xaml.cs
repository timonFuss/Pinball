using Client.Models;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Client.View.UserControls
{
    public class GridData
    {
        public BitmapImage Image { get; set; }
        public string Name { get; set; }
    }
    
    /// <summary>
    /// Interaktionslogik für EditorView.xaml
    /// </summary>
    public partial class EditorView : UserControl
    {
        private Dictionary<int, Image> idImageDict;
        private string picturePath = "pack://application:,,,/Resources/Game_Pictures/";
        private bool draggedFromList = false;
        private int MENULEISTENABZUG = 540;
        private int LEISTENABZUG = 25;

        private float posXOnImage;
        private float posYOnImage;

        //um das Bild auch draggen zu können
        private bool imageDragging;
        private bool placeNewImage;
        private Image actImage;
        private float draggingImageOriginalPosX;
        private float draggingImageOriginalPosY;


        //Linienzeug
        private const int LINELAYER = 4;

        private bool topLineDragging;
        private bool leftLineDragging;

        private List<Line> lineList;
        private Line actLine;

        public EditorView()
        {
            InitializeComponent();
            lineList = new List<Line>();
            generateEditorField();
            EditorModel.Instance.SetNewElementEvent += setNewElement;
            EditorModel.Instance.deleteElementEvent += deleteElement;
            EditorModel.Instance.moveElementEvent += moveElement;
            fillPictureList();
        }

        /// <summary>
        /// Fügt der Liste von Editorobjekten alle gewünschten Items hinzu
        /// </summary>
        public void fillPictureList()
        {
            _gridList = new ObservableCollection<GridData>();

            foreach (String picture in GamePictures.Instance.pictureDict.Values)
            {
                if (picture.Contains("Flipper") || picture.Contains("Ball") || picture.Contains("Libero") || picture.Contains("goal") || picture.Contains("Plunger"))
                    continue;

                BitmapImage myBitmapImage = new BitmapImage(new Uri("pack://application:,,,/Resources/Game_Pictures/" + picture));
                GridData gridData = new GridData();
                gridData.Image = myBitmapImage;
                string url = myBitmapImage.ToString();
                string[] splittedString = url.Split('_');
                string filename;
                if (splittedString.Length > 4)
                {
                    filename = splittedString[splittedString.Length-3] + " " + splittedString[splittedString.Length - 2] + " " + splittedString[splittedString.Length - 1];
                }
                else
                {
                    filename = splittedString[splittedString.Length - 2] + " " + splittedString[splittedString.Length - 1];
                }                
                string[] filenameWithOutEnding = filename.Split('.');
                gridData.Name = filenameWithOutEnding[0];
                _gridList.Add(gridData);
            }

            listView.ItemsSource = _gridList;
        }

        //Property der Liste von Editorobjekten
        private ObservableCollection<GridData> _gridList;
        public ObservableCollection<GridData> GridList
        {
            get
            {
                return _gridList;
            }
            set
            {
                _gridList = value;
            }
        }
        

        /// <summary>
        /// generiert das Editorfeld und fügt zu allen Elementen das passende Bild ein
        /// </summary>
        private void generateEditorField()
        {
            idImageDict = new Dictionary<int, Image>();
            
            foreach(int id in EditorModel.Instance.GameElements.Keys)
            {
                Image actImage = new Image { Source = new BitmapImage(new Uri(picturePath + GamePictures.Instance.pictureDict[EditorModel.Instance.GameElements[id].Type])) };
                if (EditorModel.Instance.GameElements[id].Type == ElementType.BALL)
                {
                    continue;
                }
                actImage.MouseDown += CanvasImage_MouseDown;
                actImage.MouseLeftButtonDown += CanvasImage_MouseLeftButtonDown;
                EditorCanvas.Children.Add(actImage);
                actImage.SetValue(Canvas.TopProperty, (double)EditorModel.Instance.GameElements[id].PosY);
                actImage.SetValue(Canvas.LeftProperty, (double)EditorModel.Instance.GameElements[id].PosX);

                idImageDict.Add(id, actImage);
                setLayer(EditorModel.Instance.GameElements[id].Type, actImage);
            }
        }

        /// <summary>
        /// setzt die richtige Ebene für Plunger, Pfosten, Strafraum, Tor, Flipper und Ball, damit sie sich richtig überlappen
        /// </summary>
        private void setLayer(ElementType elementType, Image actImage)
        {
            if (elementType == ElementType.PLUNGER
                || elementType == ElementType.GOALPOST_PLAYER_1
                || elementType == ElementType.GOALPOST_PLAYER_2
                || elementType == ElementType.GOALPOST_PLAYER_3
                || elementType == ElementType.GOALPOST_PLAYER_4
                || elementType == ElementType.GOALAREA_PLAYER_1
                || elementType == ElementType.GOALAREA_PLAYER_2
                || elementType == ElementType.GOALAREA_PLAYER_3
                || elementType == ElementType.GOALAREA_PLAYER_4
                || elementType == ElementType.CURVE_0
                || elementType == ElementType.CURVE_1
                || elementType == ElementType.CURVE_2
                || elementType == ElementType.CURVE_3)
                Canvas.SetZIndex(actImage, 0);
            else if (elementType == ElementType.BALL
                || elementType == ElementType.FLIPPER_PLAYER_1_LEFT
                || elementType == ElementType.FLIPPER_PLAYER_1_RIGHT
                || elementType == ElementType.FLIPPER_PLAYER_2_LEFT
                || elementType == ElementType.FLIPPER_PLAYER_2_RIGHT
                || elementType == ElementType.FLIPPER_PLAYER_3_LEFT
                || elementType == ElementType.FLIPPER_PLAYER_3_RIGHT
                || elementType == ElementType.FLIPPER_PLAYER_4_LEFT
                || elementType == ElementType.FLIPPER_PLAYER_4_RIGHT)
                Canvas.SetZIndex(actImage, 1);
            else if (elementType == ElementType.GOAL_PLAYER_1
                || elementType == ElementType.GOAL_PLAYER_2
                || elementType == ElementType.GOAL_PLAYER_3
                || elementType == ElementType.GOAL_PLAYER_4)
                Canvas.SetZIndex(actImage, 2);
            else if (elementType == ElementType.LIBERO_PLAYER_1
                || elementType == ElementType.LIBERO_PLAYER_2
                || elementType == ElementType.LIBERO_PLAYER_3
                || elementType == ElementType.LIBERO_PLAYER_4)
                Canvas.SetZIndex(actImage, 3);
            else
                Canvas.SetZIndex(actImage, 4);

        }



        /// <summary>
        /// Neue Element werden dem Dictionary hinzugefügt (und auch das passende Bild erstellt)
        /// </summary>
        private void setNewElement(int id, float posX, float posY, ElementType elementType)
        {

            Application.Current.Dispatcher.Invoke((Action)delegate {

                Image newImage = new Image { Source = new BitmapImage(new Uri(picturePath + GamePictures.Instance.pictureDict[elementType])) };
                newImage.MouseDown += CanvasImage_MouseDown;
                newImage.MouseLeftButtonDown += CanvasImage_MouseLeftButtonDown;
                EditorCanvas.Children.Add(newImage);
                newImage.SetValue(Canvas.TopProperty, (double)posY);
                newImage.SetValue(Canvas.LeftProperty, (double)posX);
                idImageDict.Add(id, newImage);
            });          
            
        }

        /// <summary>
        /// Das Element wird aus dem Dictionary und dem Canvas entfernt und somit all seine Daten
        /// </summary>
        private void deleteElement(int id)
        {
            Application.Current.Dispatcher.Invoke((Action)delegate {
                EditorCanvas.Children.Remove(idImageDict[id]);
                idImageDict.Remove(id);
            });
        }

        /// <summary>
        /// Das Element wird aktualisiert und an die gewünschte neue Position versetzt
        /// </summary>
        private void moveElement(int id, float newPosX, float newPosY)
        {
            Application.Current.Dispatcher.Invoke((Action)delegate {
                foreach (FrameworkElement element in EditorCanvas.Children)
                {
                    if (element.GetType() == typeof(Image))
                    {
                        Image img = (Image)element;
                        if (idImageDict[id] == img)
                        {
                            img.SetValue(Canvas.TopProperty, (double)newPosY);
                            img.SetValue(Canvas.LeftProperty, (double)newPosX);
                        }
                    }
                }
            });
        }

        /// <summary>
        /// Berechnet die Mausposition auf dem Bild im Canvas
        /// </summary>
        private void CanvasImage_MouseLeftButtonDown(object sender, MouseEventArgs e)
        {
            imageDragging = true;
            actImage = (Image)sender;

            Point mousePosition = e.GetPosition(this);

            Point mousePosOnCanvas = new Point();
            mousePosOnCanvas.X = mousePosition.X - (LEISTENABZUG + MENULEISTENABZUG);
            mousePosOnCanvas.Y = mousePosition.Y - LEISTENABZUG;

            posXOnImage = Convert.ToSingle(mousePosOnCanvas.X - (double)actImage.GetValue(Canvas.LeftProperty));
            posYOnImage = Convert.ToSingle(mousePosOnCanvas.Y - (double)actImage.GetValue(Canvas.TopProperty));

            draggingImageOriginalPosX = Convert.ToSingle((double)actImage.GetValue(Canvas.LeftProperty));
            draggingImageOriginalPosY = Convert.ToSingle((double)actImage.GetValue(Canvas.TopProperty));

            draggedFromList = false;
        }


        /// <summary>
        /// prüft den Tastendruck, wenn es das Mausrad ist dann wird das entsprechende Bild/Objekt gesucht und der Löschwunsch dem Server weitergeleitet
        /// </summary>
        private void CanvasImage_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (e.ChangedButton == MouseButton.Middle && e.ButtonState == MouseButtonState.Pressed)
            {
                var id = idImageDict.FirstOrDefault(x => x.Value == (Image)sender).Key;
                EditorModel.Instance.EditorProducer.DeleteElement(id);
            }
        }

        /// <summary>
        /// Reagiert auf einen Klick im EditorGrid auf den einzelnen Bildern
        /// </summary>
        private void ListImage_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            draggedFromList = true;

            //Speichert die Mausposition
            Point mousePosition = e.GetPosition(null);

            //holt das verschobene ListViewItem
            ListView lView = sender as ListView;
            ListViewItem listViewItem = FindAncestor<ListViewItem>((DependencyObject)e.OriginalSource);

            //Findet die Daten hinter dem ListViewItem
            BitmapImage img = ((GridData)(listView.ItemContainerGenerator.ItemFromContainer(listViewItem))).Image;
            actImage = new Image { Source = img };
            posXOnImage = LEISTENABZUG;
            posYOnImage = LEISTENABZUG;
            imageDragging = true;
            placeNewImage = true;
        }

        //Hilfskasse zum durchsuchen des VisualTree
        private T FindAncestor<T>(DependencyObject current) where T : DependencyObject
        {
            do
            {
                if (current is T)
                {
                    return (T)current;
                }
                current = VisualTreeHelper.GetParent(current);
            }
            while (current != null);
            return null;
        }

        /// <summary>
        /// Aktion für die Ausführung von Tastendrücken von Redo (STRG+Y) / Undo (STRG+Z)
        /// </summary>
        private void HandleKeyDown(object sender, KeyEventArgs e)
        {
            if (Keyboard.IsKeyDown(Key.LeftCtrl) || Keyboard.IsKeyDown(Key.RightCtrl))
            {
                switch (e.Key)
                {
                    case Key.Z:
                        EditorModel.Instance.EditorProducer.Undo();
                        break;
                    case Key.Y:
                        EditorModel.Instance.EditorProducer.Redo();
                        break;
                    default: return;
                }
            }
        }

        private void UserControl_Loaded(object sender, RoutedEventArgs e)
        {
            var window = Window.GetWindow(this);
            window.KeyDown += HandleKeyDown;
            this.Focusable = true;
            this.Focus();
        }

        //Linienzeug

        /// <summary>
        /// Leiste zum erzeugen von neuen Hilflinien (von oben)
        /// </summary>
        private void RulerImage_MouseLeftButtonDown_Top(object sender, MouseButtonEventArgs e)
        {
            topLineDragging = true;
            actLine = new Line { X1 = 0, X2 = 800, Stroke = new SolidColorBrush { Color = Colors.White }, StrokeThickness = 2 };
            Canvas.SetZIndex(actLine, LINELAYER);
            actLine.MouseLeftButtonDown += TopLine_MouseLeftButtonDown;
            lineList.Add(actLine);
            EditorCanvas.Children.Add(actLine);
        }

        /// <summary>
        /// Reaktion auf den Klick auf einer horizontalen Linie
        /// </summary>
        private void TopLine_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            actLine = (Line)sender;
            topLineDragging = true;
            
        }

        /// <summary>
        /// Leiste zum erzeugen von neuen Hilflinien (von links)
        /// </summary>
        private void RulerImage_MouseLeftButtonDown_Left(object sender, MouseButtonEventArgs e)
        {
            leftLineDragging = true;
            actLine = new Line { Y1 = 0, Y2 = 800, Stroke = new SolidColorBrush { Color = Colors.White }, StrokeThickness = 2 };
            Canvas.SetZIndex(actLine, LINELAYER);
            actLine.MouseLeftButtonDown += LeftLine_MouseLeftButtonDown;
            lineList.Add(actLine);
            EditorCanvas.Children.Add(actLine);
        }

        /// <summary>
        /// Reaktion auf den Klick auf einer vertikalen Linie
        /// </summary>
        private void LeftLine_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            actLine = (Line)sender;
            leftLineDragging = true;
        }

        /// <summary>
        /// Reaktion auf das dropppen einer Linie außerhalb des Canvas
        /// </summary>
        private void RulerImage_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            if (actLine != null)
            {
                EditorCanvas.Children.Remove(actLine);
                lineList.Remove(actLine);
                topLineDragging = false;
                leftLineDragging = false;
                actLine = null;
            }            
        }

        /// <summary>
        /// Guckt, wo und wann welches Objekt in welchem Bereich bewegt wird und setzt je nach Bedingung deren benötigten Positionen
        /// </summary>
        private void Grid_MouseMove(object sender, MouseEventArgs e)
        {
            Point mousePosition = e.GetPosition(this);
            Point mousePosOnCanvas = new Point();
            mousePosOnCanvas.X = mousePosition.X - (LEISTENABZUG + MENULEISTENABZUG);
            mousePosOnCanvas.Y = mousePosition.Y - LEISTENABZUG;

            if (mousePosOnCanvas.X>=0 && mousePosOnCanvas.Y >= 0)
            {
                if (placeNewImage)
                {
                    EditorCanvas.Children.Add(actImage);
                    placeNewImage = false;
                }
                if (topLineDragging)
                {
                    actLine.Y1 = mousePosOnCanvas.Y;
                    actLine.Y2 = mousePosOnCanvas.Y;
                }
                else if (leftLineDragging)
                {
                    actLine.X1 = mousePosOnCanvas.X;
                    actLine.X2 = mousePosOnCanvas.X;
                }
                else if (imageDragging)
                {
                    Canvas.SetLeft(actImage, mousePosOnCanvas.X - posXOnImage);
                    Canvas.SetTop(actImage, mousePosOnCanvas.Y - posYOnImage);
                }
            }
        }

        /// <summary>
        /// schickt dem Server den Wunsch eines neu gesetzten Objekts oder aber eines verschobenem Objekts
        /// </summary>
        private void Grid_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            Point mousePosition = e.GetPosition(this);
            Point mousePosOnCanvas = new Point();
            mousePosOnCanvas.X = mousePosition.X - (LEISTENABZUG + MENULEISTENABZUG);
            mousePosOnCanvas.Y = mousePosition.Y - LEISTENABZUG;

            if (mousePosOnCanvas.X >= 0 && mousePosOnCanvas.Y >= 0)
            {
                if (topLineDragging || leftLineDragging)
                {
                    topLineDragging = false;
                    leftLineDragging = false;
                    actLine = null;
                }
                else if (imageDragging)
                {
                    if (draggedFromList)
                    {
                        EditorCanvas.Children.Remove(actImage);
                        ElementType elementType = getElementType(actImage);
                        EditorModel.Instance.EditorProducer.SetElement((float)mousePosOnCanvas.X - posXOnImage, (float)mousePosOnCanvas.Y - posYOnImage, elementType.ToString());
                    }
                    else
                    {
                        actImage.SetValue(Canvas.LeftProperty, (double)draggingImageOriginalPosX);
                        actImage.SetValue(Canvas.TopProperty, (double)draggingImageOriginalPosY);
                        int id = idImageDict.FirstOrDefault(x => x.Value == actImage).Key;
                        EditorModel.Instance.EditorProducer.MoveElement(id, ((float)mousePosOnCanvas.X - draggingImageOriginalPosX) - posXOnImage, ((float)mousePosOnCanvas.Y - draggingImageOriginalPosY) - posYOnImage);
                    }

                    imageDragging = false;
                }
            }
        }

        /// <summary>
        /// Reaktion wenn das Objekt auf den Papierkorb gedroppt wird -> löscht das ausgewählte Element
        /// </summary>
        private void DeleteCanvas_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            int id = idImageDict.FirstOrDefault(x => x.Value == actImage).Key;
            EditorModel.Instance.EditorProducer.DeleteElement(id);
            EditorCanvas.Children.Remove(actImage);
            actImage = null;
            imageDragging = false;
        }

        /// <summary>
        /// Hilfmethode um an das passende Bild zu kommen
        /// </summary>
        private ElementType getElementType(Image image)
        {
            string url = image.Source.ToString();
            string[] splittedString = url.Split('/');
            string filename = splittedString[splittedString.Length - 1];
            return GamePictures.Instance.pictureDict.FirstOrDefault(x => x.Value == filename).Key;
        }

        /// <summary>
        /// Löscht alle momentan platzierten Hilfslinien
        /// </summary>
        private void killLines_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            foreach (Line actLine in lineList)
            {
                EditorCanvas.Children.Remove(actLine);
            }
            lineList.Clear();
        }
    }
}
