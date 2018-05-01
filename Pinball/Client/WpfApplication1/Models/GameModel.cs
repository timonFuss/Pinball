using Client.Communication;
using Client.View;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.Models
{
    /// <summary>
    /// beinhaltet alles, was auf dem Spielfeld liegt, also im Grunde das gesamte Game (nur Datenobjekte, keine UI-Elemente)
    /// ist als Singleton angelegt, damit in Code-Behind-Datei von GameView.xaml darauf zugegriffen werden kann
    /// beinhaltet außerdem den GameProducer, da die GameView direkt auf ihn zugreifen muss, wird hier als leicht unschöne Lösung deponiert
    /// </summary>
    class GameModel
    {
        private static GameModel instance;
        public Dictionary<int, GameElement> GameElements { get; set; }
        private Dictionary<int, bool> changeableDict;

        //gehört nicht wirklich dazu, soll aber nicht selbst zum Singleton werden, also so (herr Weitz ist Schuld):
        public GameProducer GameProducer { get; set; }
        public GameConsumer GameConsumer { get; set; }

        public delegate void FillDictComplete();
        public event FillDictComplete FillDictCompleteEvent;

        public delegate void TenSecLeft();
        public event TenSecLeft TenSecLeftEvent;

        /// <summary>
        /// instanziiert Dictionarys
        /// </summary>
        private GameModel()
        {
            GameElements = new Dictionary<int, GameElement>();
            changeableDict = new Dictionary<int, bool>();
        }

        /// <summary>
        /// setzt X-Position eines GameElements, wird beim Laden aufgerufen
        /// </summary>
        public void SetPosX(int id, float posX)
        {
            if (GameElements.ContainsKey(id))
            {
                GameElements[id].PosX = posX;
            }
            else
            {
                GameElements[id] = new GameElement { PosX = posX };
            }
        }

        /// <summary>
        /// setzt Y-Position eines GameElements, wird beim Laden aufgerufen
        /// </summary>
        public void SetPosY(int id, float posY)
        {
            if (GameElements.ContainsKey(id))
            {
                GameElements[id].PosY = posY;
            }
            else
            {
                GameElements[id] = new GameElement { PosY = posY };
            }
        }

        /// <summary>
        /// setzt Typ eines GameElements, wird beim Laden aufgerufen, abhängig davon wird später das Bild geladen
        /// </summary>
        public void SetType(int id, string type)
        {
            ElementType elementType = (ElementType)Enum.Parse(typeof(ElementType), type);
            if (GameElements.ContainsKey(id))
            {
                GameElements[id].Type = elementType;
            }
            else
            {
                GameElements[id] = new GameElement { Type = elementType };
            }
        }

        /// <summary>
        /// befüllt das changeable-Dict des GameModels, legt fest welche der zu ladenen Elemente in der FillDictionarys-Methode in welches Dict gefüllt werden
        /// </summary>
        public void SetChangeableInfo(int id, bool changeable)
        {
            changeableDict[id] = changeable;
        }

        /// <summary>
        /// wird aufgerufen, sobald der Consumer signalisiert, dass der das Update-Paket fertig verarbeitet hat
        /// ordnet anhand des changeable-Dicts die GameElements aus allElements in AgileElements und StaticElements
        /// </summary>
        public void PreperateDict()
        {
            // jedes GameElement, das Changeable ist wird in ein gameUpdateELement umgewandelt
            foreach(int id in changeableDict.Keys)
            {
                if (changeableDict[id])
                    GameElements[id] = new GameUpdateElement(GameElements[id]);
            }
            FillDictCompleteEvent();
        }

        public void NotifyTenSecLeft()
        {
            TenSecLeftEvent();
        }

        /// <summary>
        /// wird bei der Verarbeitung des GameUpdatepackets aufgerufen, verändert x-Position des GameElements mit der id
        /// </summary>
        public void UpdatePosX(int id, float posX)
        {
            GameElements[id].PosX = posX;
        }


        /// <summary>
        /// wird bei der Verarbeitung des GameUpdatepackets aufgerufen, verändert y-Position des GameElements mit der id
        /// </summary>
        public void UpdatePosY(int id, float posY)
        {
            GameElements[id].PosY = posY;
        }

        /// <summary>
        /// wird bei der Verarbeitung des GameUpdatepackets aufgerufen, verändert Rotation des GameElements mit der id
        /// </summary>
        public void UpdateRotation(int id, float Rotation)
        {
            ((GameUpdateElement)GameElements[id]).Rotation = Rotation;
        }

        // Instanz-Property für das Singleton
        public static GameModel Instance
        {
            get
            {
                if (instance == null)
                    instance = new GameModel();
                return instance;
            }
        }

        public void ClearGameModel()
        {
            GameElements.Clear();
            changeableDict.Clear();
        }
    }
}
