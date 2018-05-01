using Client.Communication;
using Client.Helper;
using System.Text.RegularExpressions;
using System.Windows;
using System.Windows.Input;

namespace Client.ViewModels.UserControls
{
    class LoginViewModel:ViewModelBase
    {
        private const string MAIN_TOSERVER = "mainToServer";
        private string _ip = "localhost";
        private string _name = "";
        private ICommand _loginCommand;

        public delegate void ShowCreditsCommandHandler();
        public event ShowCreditsCommandHandler ShowCredits;
        private ICommand _aboutEvent;

        private string _errorText = "Hidden";
        private Visibility _errorVisibility = Visibility.Hidden;
        private Visibility _validUsername = Visibility.Hidden;

        private MainProducer mainProducer;
        private MainConsumer mainConsumer;


        public LoginViewModel()
        {
            mainConsumer.LoginOkEvent += HandleLoginOk;
        }

        public Visibility ValidUsername
        {
            get
            {
                return _validUsername;
            }
            set
            {
                _validUsername = value;
                OnPropertyChanged("ValidUsername");
            }
        }


        //speichert den namen und feuert OnPropertyChanged Event wenn sich dieser auf der Gui ändert
        public string Name
        {
            get
            {
                return _name;
            }
            set
            {
                if (_name != value)
                {
                    _name = value;
                    OnPropertyChanged("Name");
                }
            }
        }

        //speichert die ip und feuert OnPropertyChanged Event wenn sich diese auf der Gui ändert.
        public string Ip
        {
            get
            {
                return _ip;
            }
            set
            {
                if (_ip != value)
                {
                    _ip = value;
                    OnPropertyChanged("IP");
                }
            }
        }

        public string ErrorText
        {
            get
            {
                return _errorText;
            }
            set
            {
                if (_errorText != value)
                {
                    _errorText = value;
                    OnPropertyChanged("ErrorText");
                }
            }
        }

        public Visibility ErrorVisibility
        {
            get
            {
                return _errorVisibility;
            }
            set
            {
                _errorVisibility = value;
                OnPropertyChanged("ErrorVisibility");
            }
        }


        //Command der an die MainWindow xaml gebunden wird 
        public ICommand LoginCommand
        {
            get
            {
                if (_loginCommand == null)
                {
                    _loginCommand = new ActionCommand(dummy => TryLogin(_ip, _name), dummy => validName()  && this.Ip != "");
                }
                return _loginCommand;
            }
        }

        public ICommand AboutEvent
        {
            get
            {
                if (_aboutEvent == null)
                {
                    _aboutEvent = new ActionCommand(dummy => ShowCredits());
                }
                return _aboutEvent;
            }
        }


        private bool validName()
        {
            if (Name != "")
            {
                Regex rg = new Regex(@"^[a-zA-Z0-9\s-_.]*$");
                if (rg.IsMatch(Name))
                {
                    ValidUsername = Visibility.Hidden;
                    return true;
                }
            }
            ValidUsername = Visibility.Visible;
            return false;
        }



        public LoginViewModel(MainConsumer mainConsumer, MainProducer mainProducer)
        {
            this.mainConsumer = mainConsumer;
            this.mainProducer = mainProducer;
            mainConsumer.LoginOkEvent += HandleLoginOk;
        }

        /// <summary>
        /// Falls der Login nicht erfolgreich war, wird eine Warnung angezeigt.
        /// </summary>
        /// <param name="isOk"></param>
        private void HandleLoginOk(bool isOk)
        {
            if (!isOk)
            {
                ErrorText = "Dieser Username ist schon vergeben!";
                ErrorVisibility = Visibility.Visible;
            }
            else
                ErrorVisibility = Visibility.Hidden;
        }

        /// <summary>
        /// Der Client versucht sich mit dem angegebenen Usernamen und der angegebenen IP-Adresse einzuloggen.
        /// Dazu wird der MainProducer und MainConsumer initialisiert und der Username wird an der Server gesendet.
        /// </summary>
        /// <param name="ip"></param>
        /// <param name="name"></param>
        private void TryLogin(string ip, string name)
        {
            //BrokerManager, Consumer, Producer erstellen
            try
            {
                BrokerManager.Instance.Init(ip);
                BrokerManager.Instance.StartConnection();
                mainProducer.Init(BrokerManager.Instance.GetSession(), MAIN_TOSERVER, BrokerManager.Instance.GetClientDestination());
                mainConsumer.Init(BrokerManager.Instance.GetSession(), BrokerManager.Instance.GetClientDestination());
                mainProducer.Login(name);
            }
            catch
            {
                ErrorText = "Server nicht gefunden!";
                ErrorVisibility = Visibility.Visible;
            }            
        }
    }
}
