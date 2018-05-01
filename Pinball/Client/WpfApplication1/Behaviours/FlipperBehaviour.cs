using Client.Models;
using Client.View;
using Client.View.UserControls;
using System.Numerics;
using System.Windows.Media;
using TestFlipper.Behaviours.Parent;

namespace Client.Behaviours
{
    //Drehe Objekt um Achse
    public class FlipperBehaviour : UIElementBehaviour
    {
        private int id;
        private RotateTransform rotateTransform;
        private const float ROTATIONOFFSET = 12;

        private Vector2 rotationPoint;
        private const float FLIPPER_WIDTH = 64;
        private const float FLIPPER_HEIGHT = 23;


        public FlipperBehaviour(int id, string typeString)
        {
            this.id = id;
            if (typeString.Equals(ElementType.FLIPPER_PLAYER_1_LEFT.ToString()) ||
                typeString.Equals(ElementType.FLIPPER_PLAYER_2_LEFT.ToString()))
            {
                rotationPoint = new Vector2(ROTATIONOFFSET, FLIPPER_HEIGHT / 2);
            }
            else if (typeString.Equals(ElementType.FLIPPER_PLAYER_1_RIGHT.ToString()) ||
                typeString.Equals(ElementType.FLIPPER_PLAYER_2_RIGHT.ToString()))
            {
                rotationPoint = new Vector2(FLIPPER_WIDTH - ROTATIONOFFSET, FLIPPER_HEIGHT / 2);
            }

            if (typeString.Equals(ElementType.FLIPPER_PLAYER_3_LEFT.ToString()) ||
                typeString.Equals(ElementType.FLIPPER_PLAYER_4_LEFT.ToString()))
            {
                rotationPoint = new Vector2(FLIPPER_HEIGHT / 2, ROTATIONOFFSET);
            }
            else if (typeString.Equals(ElementType.FLIPPER_PLAYER_3_RIGHT.ToString()) ||
                typeString.Equals(ElementType.FLIPPER_PLAYER_4_RIGHT.ToString()))
            {
                rotationPoint = new Vector2(FLIPPER_HEIGHT / 2, FLIPPER_WIDTH - ROTATIONOFFSET);
            }
        }

        public override void Start()
        {
            //startUpdate
        }

        public override void Update()
        {
            //rotateTransform = new RotateTransform(rotateTransform.Angle + 0.5 * deltaTime, 0, 0);
            //uiElement.RenderTransform = rotateTransform;

            rotateTransform = new RotateTransform(((GameUpdateElement)GameModel.Instance.GameElements[id]).Rotation, rotationPoint.X, rotationPoint.Y);
            uiElement.RenderTransform = rotateTransform;
        }

        public override void Stop()
        {
            //stopUpdate
        }

    }
}
