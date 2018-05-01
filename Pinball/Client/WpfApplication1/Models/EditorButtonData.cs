using Client.Helper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace Client.Models
{
    class EditorButtonData
    {

        private string _editorID;
        private string _roomName;
        private int _nrplayer;
        private ICommand _openEditorCommand;

        public delegate void JoinEditorCommandHandler(string id);
        public event JoinEditorCommandHandler JoinEditorEvent;

        public ICommand OpenEditorCommand
        {
            get
            {
                if (_openEditorCommand == null)
                {

                    _openEditorCommand = new ActionCommand(dummy => JoinEditorEvent(_editorID), null);
                }
                //return null?
                return _openEditorCommand;
            }
        }

        public string EditorID
        {
            get
            {
                return _editorID;
            }
            set
            {
                _editorID = value;
            }
        }
        public string RoomName
        {
            get
            {
                return _roomName;
            }
            set
            {
                _roomName = value;
            }
        }

        public int Player
        {
            get
            {
                return _nrplayer;
            }
            set
            {
                _nrplayer = value;
            }
        }


        public EditorButtonData(string id, string name, int nr)
        {
            this._editorID = id;
            this._roomName = name;
            this._nrplayer = nr;
        }
    }
}
