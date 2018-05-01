using Client.View;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Media;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media;

namespace Client.Models
{
    class SoundManager
    {
        private Dictionary<ElementType, Uri> typeSoundPathDict;
        private const string soundPath = @"../../Resources/Sounds/";

        private const int SOUNDDELAY = 80;
        private const int COUNTDOWNDELAY = 192;

        Uri wall_Uri = new Uri(soundPath + "ball_wall.wav", UriKind.Relative);
        Uri flipper_up_Uri = new Uri(soundPath + "flipper_up.wav", UriKind.Relative);
        Uri flipper_down_Uri = new Uri(soundPath + "flipper_down.wav", UriKind.Relative);
        Uri countdown_Uri = new Uri(soundPath + "countdown.wav", UriKind.Relative);
        Uri shooutout_Uri = new Uri(soundPath + "shootout.wav", UriKind.Relative);
        Uri bing_kurz_Uri = new Uri(soundPath + "bing_kurz.wav", UriKind.Relative);
        Uri boing2_kurz_Uri = new Uri(soundPath + "boing2_kurz.wav", UriKind.Relative);
        Uri boing_kurz_Uri = new Uri(soundPath + "boing_kurz.wav", UriKind.Relative);
        Uri schnack_kurz_Uri = new Uri(soundPath + "schnack_kurz.wav", UriKind.Relative);
        Uri libero_Uri = new Uri(soundPath + "libero.wav", UriKind.Relative);
        Uri goal_Uri = new Uri(soundPath + "goal.wav", UriKind.Relative);

        // eigener MediaPlayer für Countdown, um das Garbage-Collector-Problem zu lösen, die HideListe könnte für den lang laufenden Countdown-Sound zu schnell leergeräumt werden
        private MediaPlayer countDownMediaPlayer = new MediaPlayer();

        private LinkedList<MediaPlayer> hideFromGarbageCollectorList = new LinkedList<MediaPlayer>();


        public SoundManager()
        {
            
            //flipper_up_MediaPlayer.Load(Client.Properties.Resources.flipper_up);
            //flipper_down_MediaPlayer.Load(Client.Properties.Resources.flipper_down);
            //countdown_MediaPlayer.Load(Client.Properties.Resources.countdown);
            //shooutout_MediaPlayer.Load(Client.Properties.Resources.shootout);
            //boing2_kurz_MediaPlayer.Load(Client.Properties.Resources.boing2_kurz);
            //boing_kurz_MediaPlayer.Load(Client.Properties.Resources.boing_kurz);
            //bing_kurz_MediaPlayer.Load(Client.Properties.Resources.bing_kurz);
            //schnack_kurz_MediaPlayer.Load(Client.Properties.Resources.schnack_kurz);
            //libero_MediaPlayer.Load(Client.Properties.Resources.libero);
            //goal_MediaPlayer.Load(Client.Properties.Resources.goal);
            typeSoundPathDict = new Dictionary<ElementType, Uri>();

            typeSoundPathDict[ElementType.STEIN_0] = boing2_kurz_Uri;
            typeSoundPathDict[ElementType.STEIN_1] = boing_kurz_Uri;
            typeSoundPathDict[ElementType.STEIN_2] = bing_kurz_Uri;
            
            typeSoundPathDict[ElementType.WAND_STARK_0] = schnack_kurz_Uri;
            typeSoundPathDict[ElementType.WAND_STARK_1] = schnack_kurz_Uri;
            typeSoundPathDict[ElementType.WAND_STARK_2] = schnack_kurz_Uri;
            typeSoundPathDict[ElementType.WAND_STARK_3] = schnack_kurz_Uri;
            typeSoundPathDict[ElementType.WAND_SCHWACH_0] = schnack_kurz_Uri;
            typeSoundPathDict[ElementType.WAND_SCHWACH_1] = schnack_kurz_Uri;
            typeSoundPathDict[ElementType.WAND_SCHWACH_2] = schnack_kurz_Uri;
            typeSoundPathDict[ElementType.WAND_SCHWACH_3] = schnack_kurz_Uri;

            typeSoundPathDict[ElementType.GOAL_PLAYER_1] = goal_Uri;
            typeSoundPathDict[ElementType.GOAL_PLAYER_2] = goal_Uri;
            typeSoundPathDict[ElementType.GOAL_PLAYER_3] = goal_Uri;
            typeSoundPathDict[ElementType.GOAL_PLAYER_4] = goal_Uri;

            
            typeSoundPathDict[ElementType.GOALPOST_PLAYER_1] = wall_Uri;
            typeSoundPathDict[ElementType.GOALPOST_PLAYER_2] = wall_Uri;
            typeSoundPathDict[ElementType.GOALPOST_PLAYER_3] = wall_Uri;
            typeSoundPathDict[ElementType.GOALPOST_PLAYER_4] = wall_Uri;

            typeSoundPathDict[ElementType.FLIPPER_PLAYER_1_LEFT] = wall_Uri;
            typeSoundPathDict[ElementType.FLIPPER_PLAYER_1_RIGHT] = wall_Uri;
            typeSoundPathDict[ElementType.FLIPPER_PLAYER_2_LEFT] = wall_Uri;
            typeSoundPathDict[ElementType.FLIPPER_PLAYER_2_RIGHT] = wall_Uri;
            typeSoundPathDict[ElementType.FLIPPER_PLAYER_3_LEFT] = wall_Uri;
            typeSoundPathDict[ElementType.FLIPPER_PLAYER_3_RIGHT] = wall_Uri;
            typeSoundPathDict[ElementType.FLIPPER_PLAYER_4_LEFT] = wall_Uri;
            typeSoundPathDict[ElementType.FLIPPER_PLAYER_4_RIGHT] = wall_Uri;
            typeSoundPathDict[ElementType.FLIPPER_PLAYER_1_LEFT] = wall_Uri;
            typeSoundPathDict[ElementType.FLIPPER_PLAYER_1_RIGHT] = wall_Uri;

            typeSoundPathDict[ElementType.PLUNGER] = wall_Uri;

            typeSoundPathDict[ElementType.CURVE_0] = wall_Uri;
            typeSoundPathDict[ElementType.CURVE_1] = wall_Uri;
            typeSoundPathDict[ElementType.CURVE_2] = wall_Uri;
            typeSoundPathDict[ElementType.CURVE_3] = wall_Uri;

            typeSoundPathDict[ElementType.LIBERO_PLAYER_1] = libero_Uri;
            typeSoundPathDict[ElementType.LIBERO_PLAYER_2] = libero_Uri;
            typeSoundPathDict[ElementType.LIBERO_PLAYER_3] = libero_Uri;
            typeSoundPathDict[ElementType.LIBERO_PLAYER_4] = libero_Uri;

        }

        // sorgt dafür, dass der Garbage-Collector den MediaPlayer nicht wegwirft bevor er fertig abgespielt hat
        // wenn mehr als 10 MediaPlayer in der Liste liegen wird das erste Element entfernt, um den Speicher nicht volllaufen zu lassen
        private void hideFromGarbageCollector(MediaPlayer mediaPlayer)
        {
            hideFromGarbageCollectorList.AddLast(mediaPlayer);
            if (hideFromGarbageCollectorList.Count > 10)
                hideFromGarbageCollectorList.RemoveFirst();
        }

        // spiele den zu elementType gehörenden Element-getroffen-Sound ab
        public void PlaySound(ElementType elementType)
        {
            Application.Current.Dispatcher.Invoke((Action) async delegate {
                await Task.Delay(SOUNDDELAY);
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.Open(typeSoundPathDict[elementType]);
                mediaPlayer.Play();
                hideFromGarbageCollector(mediaPlayer);
            });
        }

        // spiele den Wand-getroffen-Sound ab
        public void PlayWallSound()
        {
            Application.Current.Dispatcher.Invoke((Action) async delegate {
                await Task.Delay(SOUNDDELAY);
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.Open(wall_Uri);
                mediaPlayer.Play();
                hideFromGarbageCollector(mediaPlayer);
            });
        }

        // spieler den jeweiligen Flipper-Bewegt-Sound ab
        public void PlayFlipperSound(bool up)
        {
            Application.Current.Dispatcher.Invoke((Action)async delegate {
                await Task.Delay(SOUNDDELAY);
                MediaPlayer mediaPlayer = new MediaPlayer();
                if (up)
                    mediaPlayer.Open(flipper_up_Uri);
                else
                    mediaPlayer.Open(flipper_down_Uri);
                mediaPlayer.Play();
                hideFromGarbageCollector(mediaPlayer);
            });
        }

        // hier kein Sound-Delay weil es durch den Timer auf dem Client gestartet wird
        public void PlayCountdownSound()
        {
            Application.Current.Dispatcher.Invoke((Action) async delegate {
                await Task.Delay(COUNTDOWNDELAY);
                countDownMediaPlayer.Open(countdown_Uri);
                countDownMediaPlayer.Play();
            });
        }

        // spiele den ShootOut-Sound ab
        public void PlayShootoutSound()
        {
            Application.Current.Dispatcher.Invoke((Action) async delegate {
                await Task.Delay(SOUNDDELAY);
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.Open(shooutout_Uri);
                mediaPlayer.Play();
                hideFromGarbageCollector(mediaPlayer);
            });
        }
    }
}
