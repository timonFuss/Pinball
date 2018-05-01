using Client.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media;
using TestFlipper.Behaviours.Parent;

namespace Client.Behaviours
{
    class PlungerBehaviour : UIElementBehaviour
    {
        private int id;
        private RotateTransform rotateTransform;

        public PlungerBehaviour(int id)
        {
            this.id = id;
        }

        public override void Update()
        {
            uiElement.RenderTransformOrigin = new Point(0.5, 0.5);
            rotateTransform = new RotateTransform(((GameUpdateElement)GameModel.Instance.GameElements[id]).Rotation);
            uiElement.RenderTransform = rotateTransform;
        }
    }
}
