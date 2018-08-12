package com.ascendantbrain.android.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ascendantbrain.android.bakingapp.ui.RecipeListActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RecipeListActivityTest {

    @Rule public ActivityTestRule<RecipeListActivity> mActivityTestRule
            = new ActivityTestRule<>(RecipeListActivity.class);

    @Test
    public void clickRecipeButton_LoadsStepListActivity() {
        String recipeName = "Brownies";

        // Get a reference to a specific recyclerview item and click it
        //onData(anything()).inAdapterView(withId(R.id.recipe_list)).atPosition(1).perform(click());
        onView(withId(R.id.recipe_list))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(recipeName)), click()));

        // Check that the StepListActivity opens with the correct name displayed
        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(recipeName.toUpperCase())));

    }

}
