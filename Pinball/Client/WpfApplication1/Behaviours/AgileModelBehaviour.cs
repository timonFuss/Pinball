using Client.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
using TestFlipper.Behaviours.Parent;
using TestFlipper.Extensions;

namespace Client.Behaviours
{
    class AgileModelBehaviour : UIElementBehaviour
    {
        private int id;

        Vector2 oldpos;
        float lerpValue = 30f;

        public AgileModelBehaviour(int id)
        {
            this.id = id;
        }
        public override void Update()
        {
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
