package cc.ifnot.app.hello.tile;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.widget.Toast;

import cc.ifnot.libs.utils.Lg;

public class TileService extends android.service.quicksettings.TileService {

    static {
        Lg.tag("TileService");
    }

    private boolean enabled = false;

    @Override
    public void onClick() {
        super.onClick();
        Lg.d("onClick - %b", enabled);
        Toast.makeText(this, "tile clicked: " + (enabled ? "tile Y" : "tile N"), Toast.LENGTH_SHORT).show();
        getQsTile().setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().setLabel(enabled ? "tile Y" : "tile N");
        getQsTile().setIcon(Icon.createWithResource(this, enabled ?
                android.R.drawable.radiobutton_on_background : android.R.drawable.radiobutton_off_background));
        getQsTile().updateTile();
        enabled = !enabled;
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Lg.d("onTileAdded");
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Lg.d("onTileRemoved");
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Lg.d("onStartListening");
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        Lg.d("onStopListening");
    }
}
