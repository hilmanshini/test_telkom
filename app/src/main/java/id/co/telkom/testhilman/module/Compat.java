package id.co.telkom.testhilman.module;

import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

@BindingMethods({
        @BindingMethod(type = FloatingActionButton.class
                ,
                attribute = "app:srcCompat",
                method = "setImageDrawable") })
public class Compat {
}
