using Client.Communication;
using Client.View;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.Models
{
    class EditorModel
    {
        private static EditorModel instance;
        private bool loadingComplete = false;

        public Dictionary<int, GameElement> GameElements { get; set; }

        public delegate void newElement(int id, float posX, float posY, ElementType elementType);
        public event newElement SetNewElementEvent;

        public delegate void deleteElementMethodTemplate(int id);
        public event deleteElementMethodTemplate deleteElementEvent;

        public delegate void moveElementMethodTemplate(int id,float newPosX, float newPosY);
        public event moveElementMethodTemplate moveElementEvent;

        //gehört nicht wirklich dazu, soll aber nicht selbst zum Singleton werden, also so (herr Weitz ist Schuld):
        public EditorProducer EditorProducer { get; set; }

        /// <summary>
        /// instanziiert Dictionarys
        /// </summary>
        private EditorModel()
        {
            GameElements = new Dictionary<int, GameElement>();
        }

        /// <summary>
        /// setzt X-Position eines GameElements, wird beim Laden aufgerufen
        /// </summary>
        public void SetPosX(int id, float posX)
        {
            //wenn schon geladen wurde verarbeite keine weitere Ladenachricht
            if (loadingComplete)
                return;
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
            //wenn schon geladen wurde verarbeite keine weitere Ladenachricht
            if (loadingComplete)
                return;
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
            //wenn schon geladen wurde verarbeite keine weitere Ladenachricht
            if (loadingComplete)
                return;
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
        /// fügt ein neues Element dem Spiel hinzu
        /// </summary>
        public void SetElement(int id, float posX, float posY, string elementTypeString)
        {
            //wenn noch nichts geladen wurde darf auch nichts verändert werden
            if (!loadingComplete)
                return;
            ElementType elementType = (ElementType)Enum.Parse(typeof(ElementType), elementTypeString);
            GameElements[id] = new GameElement { PosX = posX, PosY = posY, Type = elementType };
            SetNewElementEvent(id, posX, posY, elementType);
        }

        /// <summary>
        /// verschiebt ein Element
        /// </summary>
        public void MoveElement(int id, float newPosX, float newPosY)
        {
            //wenn noch nichts geladen wurde darf auch nichts verändert werden
            if (!loadingComplete)
                return;
            GameElements[id].PosX = newPosX;
            GameElements[id].PosY = newPosY;
            moveElementEvent(id ,newPosX, newPosY);
        }

        /// <summary>
        /// löscht ein Element
        /// </summary>
        public void DeleteElement(int id)
        {
            //wenn noch nichts geladen wurde darf auch nichts verändert werden
            if (!loadingComplete)
                return;
            GameElements.Remove(id);
            deleteElementEvent(id);
        }

        // Instanz-Property für das Singleton
        public static EditorModel Instance
        {
            get
            {
                if (instance == null)
                    instance = new EditorModel();
                return instance;
            }
        }

        internal void SetLoadingCompleteEvent()
        {
            loadingComplete = true;
        }

        public void ClearEditorModel()
        {
            GameElements.Clear();
            loadingComplete = false;
        }
    }
}
