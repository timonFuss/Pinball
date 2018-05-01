using Client.Communication;
using Client.Helper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections.ObjectModel;
using Client.Models;
using System.Windows.Input;
using System.Windows;
using System.Windows.Threading;
using System.Text.RegularExpressions;

namespace Client.ViewModels.UserControls
{
    class EditorViewModel:ViewModelBase
    {
        private EditorProducer editorProducer;
        private EditorConsumer editorConsumer;
        private ObservableCollection<string> _editorlist = new ObservableCollection<string>();
        private DispatcherTimer dispatcherTimer = new DispatcherTimer();
        private int _time = 1;
        private string _roomName = "Editiermodus";
        private string _templateFieldName = "";

        public EditorMenuViewModel EditorMenuViewModel { set; get; }
        private string _gamefieldname = "";
        private string _validGamefieldname = "Hidden";

        public ObservableCollection<string> Editorlist
        {
            get { return _editorlist; }
            set { _editorlist = value; OnPropertyChanged("Editorlist"); }
        }


        //EditorConsumer
        public EditorViewModel(EditorProducer editorProducer, EditorConsumer editorConsumer, EditorModel editorModel)
        {
            this.editorProducer = editorProducer;
            this.editorConsumer = editorConsumer;

            dispatcherTimer.Tick += new EventHandler(DispatcherTimer_Tick);
            dispatcherTimer.Interval = new TimeSpan(0, 0, 1);

            editorConsumer.ClientsInEditorEvent += HandleClientsInEditorEvent;
            editorConsumer.FieldSavedEvent += HandleFieldSaved;
            editorConsumer.NameChangedEvent += HandleNameChanged;   
        }

        public string ValidGamefieldname
        {
            get
            {
                return _validGamefieldname;
            }
            set
            {
                _validGamefieldname = value;
                OnPropertyChanged("ValidGamefieldname");
            }
        }

        public string Gamefieldname
        {
            get
            {
                return _gamefieldname;
            }
            set
            {
                _gamefieldname = value;OnPropertyChanged("Gamefieldname");
            }
        }

        private Visibility _saveMessageVisibility=Visibility.Hidden;
        public Visibility SaveMessageVisibility
        {
            get
            {
                return _saveMessageVisibility;
            }
            set
            {
                _saveMessageVisibility = value;
                OnPropertyChanged("SaveMessageVisibility");
            }
        }

        void DispatcherTimer_Tick(object sender, EventArgs e)
        {
            if (_time > 0)
            {
                _time--;
            }
            else
            {
                dispatcherTimer.Stop();
                SaveMessageVisibility = Visibility.Hidden;
            }
        }

        //TODO
        private void HandleFieldSaved()
        {
            SaveMessageVisibility = Visibility.Visible;
            dispatcherTimer.Start();
        }

        /// <summary>
        /// Der Client erhaelt vom Server eine Liste aller Clients, die im Editor sind. Diese werden in der Editorlist gespeichert.
        /// </summary>
        /// <param name="editorlist"></param>
        private void HandleClientsInEditorEvent(string[] editorlist)
        {
            Application.Current.Dispatcher.Invoke(delegate {
                Editorlist.Clear();
            });
            foreach (string username in editorlist)
            {
                if (username != "")
                {
                    Application.Current.Dispatcher.Invoke(delegate {
                        Editorlist.Add(username); 
                    });
                }
            }

        }

        private void HandleNameChanged(string NewName)
        {
            Gamefieldname = NewName;
        }

        /// <summary>
        /// Command zum Speichern des Editorfeldes
        /// </summary>        
        private ICommand _leaveCommand;
        public ICommand LeaveCommand
        {
            get
            {
                if (_leaveCommand == null)
                {
                    _leaveCommand = new ActionCommand(dummy => LeaveEditor(), null);
                }
                return _leaveCommand;
            }
        }

        public delegate void BackToMenuCommandHandler();
        public event BackToMenuCommandHandler BackToMenuEvent;

        public void LeaveEditor()
        {
            EditorModel.Instance.ClearEditorModel();
            editorConsumer.Terminate();
            editorProducer.LeaveEditor();
            editorProducer.Terminate();
            BackToMenuEvent();
        }

        public void reset()
        {
            Gamefieldname = "";
        }

        /// <summary>
        /// Command zum Speichern des Editorfeldes
        /// </summary>        
        private ICommand _saveCommand;
        public ICommand SaveCommand
        {
            get
            {
                if (_saveCommand == null)
                {
                    _saveCommand = new ActionCommand(dummy =>editorProducer.SaveEditorGame(), null);
                }
                return _saveCommand;
            }
        }

        /// <summary>
        /// Command zum rückgängig machen des letzten Schrittes
        /// </summary> 
        private ICommand _undoCommand;
        public ICommand UndoCommand
        {
            get
            {
                if (_undoCommand == null)
                {
                    _undoCommand = new ActionCommand(dummy => editorProducer.Undo(), null);
                }
                return _undoCommand;
            }
        }

        /// <summary>
        /// Command zum wiederholen des letzten Schrittes
        /// </summary> 
        private ICommand _redoCommand;
        public ICommand RedoCommand
        {
            get
            {
                if (_redoCommand == null)
                {
                    _redoCommand = new ActionCommand(dummy => editorProducer.Redo(), null);
                }
                return _redoCommand;
            }
        }

        /// <summary>
        /// Command zum Ändern des Spielfeldnamens
        /// </summary> 
        private ICommand _changeNameCommand;
        public ICommand ChangeNameCommand
        {
            get
            {
                if (_changeNameCommand == null)
                {
                   _changeNameCommand = new ActionCommand(dummy => editorProducer.ChangeName(this.Gamefieldname),dummy => validName());
                }
                return _changeNameCommand;
            }
        }

        private bool validName()
        {
            if(Gamefieldname != "")
            {
                Regex rg = new Regex(@"^[a-zA-Z0-9-_]*$");
                if (rg.IsMatch(Gamefieldname))
                {
                    ValidGamefieldname = "Hidden";
                    return true;
                }
                ValidGamefieldname = "Visible";
            }
            return false;
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
                OnPropertyChanged("RoomName");
            }
        }

        public string TemplateName
        {
            get
            {
                return "(Vorlage: " + _templateFieldName + ")";
            }
            set
            {
                _templateFieldName = value;
                OnPropertyChanged("TemplateName");
            }
        }

    }
}
