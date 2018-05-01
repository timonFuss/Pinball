using Client.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using TestFlipper.Behaviours.Parent;
using TestFlipper.Extensions;

namespace Client.Behaviours
{
    class BallBehaviour : UIElementBehaviour
    {
        private int id;

        Vector2 oldpos;
        float lerpValue = 30f;

        private double invisibleTime;

        private double invisibleStartTime;

        private bool _invisible;
        public bool Invisible // wird auf true gesetzt, wenn das Objekt getroffen wurde
        {
            get
            {
                return _invisible;
            }
            set
            {
                _invisible = value;
                invisibleStartTime = DateTime.Now.TimeOfDay.TotalMilliseconds;
            }
        }

        public BallBehaviour(int id, double invisibleTime)
        {
            this.invisibleTime = invisibleTime;
            this.id = id;
        }

        public override void Update()
        {
            double actTime = DateTime.Now.TimeOfDay.TotalMilliseconds;
            // wenn invisible auf true steht und die Differenz zwischen aktueller zeit und Invisible-Start-Zeit kleiner ist als die gesamte Zeit für die Unsichtbarkeit
            // zeige das animations-Bild an
            if (_invisible && invisibleTime > actTime - invisibleStartTime)
            {
                uiElement.Visibility = Visibility.Hidden;
            }
            else
            {
                _invisible = false;
                uiElement.Visibility = Visibility.Visible;
            }

            //Console.WriteLine("Frameupdate");
            Vector2 newpos = new Vector2(GameModel.Instance.GameElements[id].PosX, GameModel.Instance.GameElements[id].PosY);
            Vector2 lerpedPos = Vector2.Lerp(oldpos, newpos, (float)deltaTime / lerpValue);

            //Console.WriteLine("Frameupdate");

            uiElement.SetValue(Canvas.LeftProperty, (double)lerpedPos.X);
            uiElement.SetValue(Canvas.TopProperty, (double)lerpedPos.Y);

            //uiElement.SetValue(Canvas.LeftProperty, (double)GameModel.Instance.GameElements[id].PosX);
            //uiElement.SetValue(Canvas.TopProperty, (double)GameModel.Instance.GameElements[id].PosY);

            //debuglines
            /*
            Line line = new Line();
            line.X1 = oldpos.X;
            line.Y1 = oldpos.Y;
            line.X2 = newpos.X;
            line.Y2 = newpos.Y;
            line.Stroke = red;
            gameFieldCanvas.Children.Add(line);
            */

            oldpos = uiElement.getCanvasPosition();
        }
    }
}
