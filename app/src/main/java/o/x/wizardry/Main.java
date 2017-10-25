package o.x.wizardry;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Main extends Activity {

class Wizard {
	int colorID; int setupScreenID; int crashScreenID;
	Wizard(int c, int ws, int cs) { colorID = c; setupScreenID = ws; crashScreenID = cs; }

	ImageView to(final ImageView screen) {
		((View)screen.getParent()).setBackgroundColor(getColor(colorID));
		screen.setImageDrawable(getDrawable(setupScreenID));
		fadeIn(screen).setAnimationListener(new Listener() { void onAnimEnd() {
			screen.setOnClickListener(new Listener() { void onClick() {
					screen.setImageDrawable(getDrawable(crashScreenID));
					final Runnable r = new Runnable() { public void run() { fadeOutToNextWizard(screen); }};
					screen.postDelayed(r, millisecondsUntilAutoStartingNextWizard);
					screen.setOnClickListener(new Listener() { void onClick() {
							screen.removeCallbacks(r);
							fadeOutToNextWizard(screen);
						}
					});
				}
			});
		}});
		return screen;
	}
}

final static int millisecondsUntilAutoStartingNextWizard = 5000;

Animation fadeIn(View v) {
	v.startAnimation(fadeInAnim);
	return fadeInAnim;
}

Animation fadeOutToNextWizard(View v) {
	v.setOnClickListener(null);
	fadeOutAnim.setAnimationListener(new WizardAfterAnim());
	v.startAnimation(fadeOutAnim);
	return fadeOutAnim;
}

class WizardAfterAnim extends Listener {void onAnimEnd() { wizards.next().to(screen); }}

class Listener extends AnimListener implements View.OnClickListener{
	@Override public void onClick(View _) { onClick(); } void onClick() {};
	@Override public void onAnimationEnd(Animation _) { onAnimEnd(); }  void onAnimEnd() {};
}

class AnimListener implements Animation.AnimationListener {
	@Override public void onAnimationStart(Animation _) {}
	@Override public void onAnimationEnd(Animation _) {}
	@Override public void onAnimationRepeat(Animation _) {}
}

class Nexter {
	Wizard[] d; int i = 0; Nexter(Wizard[] data) { d = data; }
	Wizard next() { Wizard s = d[i]; i++; if (i >= d.length) i = 0; return s; };
	Wizard reset() { i = 0; return next(); }
}

final Nexter wizards = new Nexter(
		new Wizard[] {
				new Wizard(R.color.win95bg, R.drawable.windows_95_setup_wizard, R.drawable.windows_95_bsod),
				new Wizard(R.color.macOS, R.drawable.macos_reinstall, R.drawable.macos_kernel_panic),
				new Wizard(R.color.ubuntu, R.drawable.ubuntu_install, R.drawable.ubuntu_install_failed),
				new Wizard(R.color.ios, R.drawable.iphone_get_started, R.drawable.iphone_recover),
				new Wizard(R.color.android, R.drawable.android_setup_wizard, R.drawable.android_recovery_mode),
				new Wizard(R.color.kde, R.drawable.kde_setup_wizard, R.drawable.linux_kernel_panic),
				new Wizard(R.color.plan9, R.drawable.plan9_install, R.drawable.plan9_success)
		}
);

View of(int ID) { return findViewById(ID); }

ImageView screen;

@Override public void onCreate(Bundle b) {
	super.onCreate(b);
	init();
	screen = (ImageView) of (R.id.screen);
}

Animation fadeInAnim;
Animation fadeOutAnim;

void init() {
	fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
	fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out);
	setContentView(R.layout.main);
}

@Override public void onResume() {
	super.onResume();
	hideSystemUI();
	wizards.reset().to(screen);
}

void hideSystemUI() {
	getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
}

}
