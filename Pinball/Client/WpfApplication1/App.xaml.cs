using Client.Models;
using Client.ViewModels.UserControls;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Threading.Tasks;
using System.Windows;

namespace Client
{
    /// <summary>
    /// Interaktionslogik für "App.xaml"
    /// </summary>
    public partial class App : Application
    {
        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);

            MainWindow window = new MainWindow();
            MainWindowViewModel context = new MainWindowViewModel();
            window.DataContext = context;
            window.Show();
            window.Closing += context.OnClosing;
        }
    }
}
