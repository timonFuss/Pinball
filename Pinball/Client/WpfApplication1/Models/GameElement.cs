using Client.View;

namespace Client.Models
{
    class GameElement
    {
        public float PosX { get; set; }
        public float PosY { get; set; }
        public ElementType Type { get; set; }
    }

    class GameUpdateElement : GameElement
    {
        public float Rotation { get; set; }

        public GameUpdateElement(GameElement gameElement)
        {
            this.PosX = gameElement.PosX;
            this.PosY = gameElement.PosY;
            this.Type = gameElement.Type;
        }
    }
}