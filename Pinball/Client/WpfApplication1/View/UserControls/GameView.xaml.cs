using Client.Behaviours;
using Client.Models;
using Client.ViewModels.UserControls;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Numerics;
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
using TestFlipper.Behaviours.Parent;
using TestFlipper.Extensions;

namespace Client.View.UserControls
{
    


    /// <summary>
    /// Interaktionslogik für GameView.xaml, hier wird MVVM nicht mehr angewendet!
    /// </summary>
    public partial class GameView : UserControl
    {
        private string picturePath = "pack://application:,,,/Resources/Game_Pictures/";
        private Dictionary<int, UIElementBehaviour> behaviourDict;
        private List<UIElement> uieELementsWithBehaviourScript;
        private SoundManager soundManager;

        private int ballBehaviourScriptId;

        private const double STEIN_VIBRATIONTIME = 128;

        private const double WAND_STARK_ANIMATIONTIME = 128;
        private const double BALLINVISIBLEAFTERGOALTIME = 1080;
        private const int IMAGEDELAY = 32;
        private const int TIMEBETWEENGOALANDSHOOUTOUT = 2000;


        public bool keyStatusFlipLeft = false;
        public bool keyStatusFlipRight = false;
        public bool keyStatusButtonUp = false;
        public bool keyStatusButtonDown = false;
        public bool keyStatusButtonLeft = false;
        public bool keyStatusButtonRight = false;
        Vector2 clickPos;

        // wird benötigt um Events an- und abzumelden
        private Window window;

        public GameView()
        {            
            InitializeComponent();
        }
        
        /// <summary>
        /// entfernt alle alten Elemente aus dem letzten Spiel, initialisiert Collections neu und hängt Events an 
        /// </summary>
        private void Init()
        {
            GameFieldCanvas.Children.Clear();
            behaviourDict = new Dictionary<int, UIElementBehaviour>();
            uieELementsWithBehaviourScript = new List<UIElement>();
            soundManager = new SoundManager();

            GameModel.Instance.FillDictCompleteEvent += generateGameField;
            GameModel.Instance.PreperateDict();

            GameModel.Instance.GameConsumer.ElementHitEvent += ElementHit;
            GameModel.Instance.GameConsumer.WallHitEvent += WallHit;
            GameModel.Instance.GameConsumer.FlipperMovedEvent += FlipperMoved;
            GameModel.Instance.GameConsumer.ShooutOutEvent += ShooutOut;
            GameModel.Instance.GameConsumer.GameOverEvent += DeactivateGameView;
            GameModel.Instance.TenSecLeftEvent += soundManager.PlayCountdownSound;            
        }

        /// <summary>
        /// erzeugt aus dem GameElements-Dict im GameModel alle Game-XAML-Objekte in der View
        /// hängt zudem an alle veränderlichen Elemente das dazugehörige Behaviour-Script
        /// </summary>
        private void generateGameField()
        {
            Dictionary<int, GameElement> gameElementsDict = GameModel.Instance.GameElements;
            foreach (int id in gameElementsDict.Keys)
            {
                Image actImage = new Image { Source = new BitmapImage(new Uri(picturePath + GamePictures.Instance.pictureDict[gameElementsDict[id].Type])) };
                GameFieldCanvas.Children.Add(actImage);
                actImage.SetValue(Canvas.TopProperty, (double)gameElementsDict[id].PosY);
                actImage.SetValue(Canvas.LeftProperty, (double)gameElementsDict[id].PosX);
                ElementType actType = gameElementsDict[id].Type;
                string aktTypeString = actType.ToString();

                UIElementBehaviour behaviourScript = null;

                // "statische" Elemente mit Kollisions-Behaviourscripts austatten
                if (actType == ElementType.WAND_STARK_0)
                {
                    behaviourScript = new ChangeImageBehaviour(picturePath + "Element_Slingshot_0_Bounce.png", WAND_STARK_ANIMATIONTIME);
                }
                else if (actType == ElementType.WAND_STARK_1)
                {
                    behaviourScript = new ChangeImageBehaviour(picturePath + "Element_Slingshot_1_Bounce.png", WAND_STARK_ANIMATIONTIME);
                }
                else if (actType == ElementType.WAND_STARK_2)
                {
                    behaviourScript = new ChangeImageBehaviour(picturePath + "Element_Slingshot_2_Bounce.png", WAND_STARK_ANIMATIONTIME);
                }
                else if (actType == ElementType.WAND_STARK_3)
                {
                    behaviourScript = new ChangeImageBehaviour(picturePath + "Element_Slingshot_3_Bounce.png", WAND_STARK_ANIMATIONTIME);
                }

                else if (actType == ElementType.STEIN_0)
                    behaviourScript = new VibrationBehaviour(STEIN_VIBRATIONTIME, 1);
                else if (actType == ElementType.STEIN_1)
                    behaviourScript = new VibrationBehaviour(STEIN_VIBRATIONTIME, 2);
                else if (actType == ElementType.STEIN_2)
                    behaviourScript = new VibrationChangeImageBehaviour(STEIN_VIBRATIONTIME, 3, picturePath + "Elemente_Bumper_L_Bounce.png");

                // "agile" GameElements mit behaviourScripts ausstatten, die Position und Rotation jedes Frame updaten
                // wenns ein Flipper ist, hänge FlipperScript an
                else if (aktTypeString.Contains("FLIPPER"))
                    behaviourScript = new FlipperBehaviour(id, aktTypeString);

                // wenns ein Plunger ist, hänge Plunger-Script an
                else if (actType == ElementType.PLUNGER)
                    behaviourScript = new PlungerBehaviour(id);

                //uiElementBehaviour für Libero anhängen
                else if (aktTypeString.Contains("LIBERO"))
                    behaviourScript = new AgileModelBehaviour(id);
                else if (actType == ElementType.BALL)
                {
                    behaviourScript = new BallBehaviour(id, BALLINVISIBLEAFTERGOALTIME);
                    ballBehaviourScriptId = id;
                }


                if (behaviourScript != null)
                {
                    actImage.addUIElementBehaviour(behaviourScript);

                    behaviourDict[id] = behaviourScript;
                    uieELementsWithBehaviourScript.Add(actImage);
                    actImage.startUpdate();
                }
                setLayer(gameElementsDict[id].Type, actImage);
            }

            GameModel.Instance.GameProducer.SignalLoadingComplete();

            // nach dem die generateGamefield aufgerufen wurde, entferne die Methode vom Event für zukünftige Spiele
            GameModel.Instance.FillDictCompleteEvent -= generateGameField;
        }

        // setzt die richtige Ebene für Plunger, Pfosten, Strafraum, Tor, Flipper und Ball, damit sie sich richtig überlappen
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
                || elementType == ElementType.GOALAREA_PLAYER_4)
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
        }

        // wenn ein Element vom Ball getroffen wird
        private async void ElementHit(int id)
        {
            soundManager.PlaySound(GameModel.Instance.GameElements[id].Type);
            
            if (behaviourDict.ContainsKey(id) && behaviourDict[id].GetType() == typeof(ChangeImageBehaviour))
            {
                await Task.Delay(IMAGEDELAY);
                ((ChangeImageBehaviour)behaviourDict[id]).ObjectHit = true;
            }  
            else if (behaviourDict.ContainsKey(id) && behaviourDict[id].GetType() == typeof(VibrationBehaviour))
            {
                await Task.Delay(IMAGEDELAY);
                ((VibrationBehaviour)behaviourDict[id]).Vibrate = true;
            }
            else if (behaviourDict.ContainsKey(id) && behaviourDict[id].GetType() == typeof(VibrationChangeImageBehaviour))
            {
                await Task.Delay(IMAGEDELAY);
                ((VibrationChangeImageBehaviour)behaviourDict[id]).Change = true;
            }
            else if (GameModel.Instance.GameElements[id].Type == ElementType.GOAL_PLAYER_1 
                || GameModel.Instance.GameElements[id].Type == ElementType.GOAL_PLAYER_2
                || GameModel.Instance.GameElements[id].Type == ElementType.GOAL_PLAYER_3
                || GameModel.Instance.GameElements[id].Type == ElementType.GOAL_PLAYER_4)
            {
                ((BallBehaviour)behaviourDict[ballBehaviourScriptId]).Invisible = true;
                await Task.Delay(IMAGEDELAY);
            }
        }

        // wenn die Wand vom Ball getroffen wurde
        private void WallHit()
        {
            soundManager.PlayWallSound();
        }

        // wenn ein Flipper bewegt wird
        private void FlipperMoved(bool up)
        {
            // klingt leider blöd  und penetrant!
            //soundManager.PlayFlipperSound(up);
        }

        // wenn ein Flipper bewegt wird
        private void ShooutOut()
        {
            soundManager.PlayShootoutSound();
        }

       

        /// <summary>
        /// wird angestoßen, wenn die linke Maustaste in der GameView gedrückt wird, merkt sich Position in clickPos
        /// </summary>
        private void mouseLeftButtonDown(object sender, MouseEventArgs e)
        {
            clickPos = new Vector2((float)e.MouseDevice.GetPosition((IInputElement)sender).X / 2f, -1 * (float)e.MouseDevice.GetPosition((IInputElement)sender).Y / 2f);
        }

        /// <summary>
        /// wird aufgerufen, wenn die Maustaste losgelassen wurde und verschickt Differenz zwischen der Reindrück-Position und der Loslass-Position als Force-Vektor
        /// </summary>
        private void mouseLeftButtonUp(object sender, MouseEventArgs e)
        {
            Vector2 pos = Vector2.Subtract(new Vector2((float)e.MouseDevice.GetPosition((IInputElement)sender).X / 2f, -1 * (float)e.MouseDevice.GetPosition((IInputElement)sender).Y / 2f), clickPos);
            GameModel.Instance.GameProducer.AddForce(pos.X, pos.Y);
        }

        // bei down: versende false
        private void HandleKeyDown(object sender, KeyEventArgs e)
        {
            bool keyAction = false;
            switch (e.Key)
            {
                case Key.W:
                    if (keyStatusButtonUp == false)
                    {
                        GameModel.Instance.GameProducer.buttonUp(keyAction);
                        keyStatusButtonUp = true;
                    }
                    break;
                case Key.S:
                    if (keyStatusButtonDown == false)
                    {
                        GameModel.Instance.GameProducer.buttonDown(keyAction);
                        keyStatusButtonDown = true;
                    }
                    break;
                case Key.A:
                    if (keyStatusButtonLeft == false)
                    {
                        GameModel.Instance.GameProducer.buttonLeft(keyAction);
                        keyStatusButtonLeft = true;
                    }
                    break;
                case Key.D:
                    if (keyStatusButtonRight == false)
                    {
                        GameModel.Instance.GameProducer.buttonRight(keyAction);
                        keyStatusButtonRight = true;
                    }
                    break;
                //Flipper
                case Key.K:
                    if (keyStatusFlipLeft == false)
                    {
                        GameModel.Instance.GameProducer.leftFlipper(keyAction);
                        keyStatusFlipLeft = true;
                    }
                    break;
                case Key.L:
                    if (keyStatusFlipRight == false)
                    {
                        GameModel.Instance.GameProducer.rightFlipper(keyAction);
                        keyStatusFlipRight = true;
                    }
                    break;
                default: return;
            }
        }

        // bei up: versende true
        private void HandleKeyUp(object sender, KeyEventArgs e)
        {
            bool keyAction = true;
            switch (e.Key)
            {
                case Key.W:
                    if (keyStatusButtonUp == true)
                    {
                        GameModel.Instance.GameProducer.buttonUp(keyAction);
                        keyStatusButtonUp = false;
                    }
                    break;
                case Key.S:
                    if (keyStatusButtonDown == true)
                    {
                        GameModel.Instance.GameProducer.buttonDown(keyAction);
                        keyStatusButtonDown = false;
                    }
                    break;
                case Key.A:
                    if (keyStatusButtonLeft == true)
                    {
                        GameModel.Instance.GameProducer.buttonLeft(keyAction);
                        keyStatusButtonLeft = false;
                    }
                    break;
                case Key.D:
                    if (keyStatusButtonRight == true)
                    {
                        GameModel.Instance.GameProducer.buttonRight(keyAction);
                        keyStatusButtonRight = false;
                    }
                    break;
                //Flipper
                case Key.K:
                    if (keyStatusFlipLeft == true)
                    {
                        GameModel.Instance.GameProducer.leftFlipper(keyAction);
                        keyStatusFlipLeft = false;
                    }
                    break;
                case Key.L:
                    if (keyStatusFlipRight == true)
                    {
                        GameModel.Instance.GameProducer.rightFlipper(keyAction);
                        keyStatusFlipRight = false;
                    }
                    break;
                default: return;
            }
        }

        private void GameFieldCanvas_Loaded(object sender, RoutedEventArgs e)
        {
            Init();
        }

        private void UserControl_Loaded(object sender, RoutedEventArgs e)
        {
            window = Window.GetWindow(this);
            window.KeyDown += HandleKeyDown;
            window.KeyUp += HandleKeyUp;
            this.Focusable = true;
            this.Focus();
        }

        /// <summary>
        /// wird aufgerufen, wenn das GameOver-Event gefeuert wird, damit BehavioursScripts gelöscht werden und alle an Events hängenden Methoden abgemeldet werden
        /// </summary>
        /// <param name="playerName"></param>
        /// <param name="score"></param>
        private void DeactivateGameView(string playerName, float score)
        {
            //Paramater ignorieren
            // alle Behaviourscripts entfernen
            foreach (UIElement uiElement in uieELementsWithBehaviourScript)
            {
                //uiElement.stopUpdate();
                uiElement.removeUIElementBehaviour();
            }
            // Key-Events wieder abmelden
            window.KeyDown -= HandleKeyDown;
            window.KeyUp -= HandleKeyUp;
            GameModel.Instance.FillDictCompleteEvent -= generateGameField;
            GameModel.Instance.GameConsumer.ElementHitEvent -= ElementHit;
            GameModel.Instance.GameConsumer.WallHitEvent -= WallHit;
            GameModel.Instance.GameConsumer.FlipperMovedEvent -= FlipperMoved;
            GameModel.Instance.GameConsumer.ShooutOutEvent -= ShooutOut;
            GameModel.Instance.GameConsumer.GameOverEvent -= DeactivateGameView;
            GameModel.Instance.TenSecLeftEvent -= soundManager.PlayCountdownSound;
        }
    }
}
