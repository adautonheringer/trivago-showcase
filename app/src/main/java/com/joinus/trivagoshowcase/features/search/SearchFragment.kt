package com.joinus.trivagoshowcase.features.search

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.joinus.trivagoshowcase.MainViewModel
import com.joinus.trivagoshowcase.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MessageCard(viewModel)
            }
        }
    }
}


@Composable
fun MessageCard(
    viewModel: MainViewModel
) {
    val state by viewModel.searchViewState.collectAsState()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth(.85f),
            color = colorResource(id = R.color.white),
            shape = CircleShape,
            elevation = 4.dp,
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "Düsseldorf",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "Jun 2 - Jun 3",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(id = R.color.black).copy(alpha = .54f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "2 guests",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(id = R.color.black).copy(alpha = .54f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "1 room",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(id = R.color.black).copy(alpha = .54f)
                        )
                    }
                }
            }
        }
        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(.75f)
                    .clip(CircleShape),
                backgroundColor = colorResource(id = R.color.blue).copy(alpha = .5f),
                color = colorResource(id = R.color.blue)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

}

@Preview
@Composable
fun PreviewMessageCard() {
//    MessageCard(isLoading = true)
}
