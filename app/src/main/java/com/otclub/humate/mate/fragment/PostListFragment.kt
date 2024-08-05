package com.otclub.humate.mate.fragment

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.MateFragmentPostListBinding
import com.otclub.humate.mate.adapter.PostListAdapter
import com.otclub.humate.mate.data.PostListFilterDTO
import com.otclub.humate.mate.viewmodel.PostViewModel
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon


class PostListFragment : Fragment() {

    private var mBinding : MateFragmentPostListBinding? = null
    private val binding get() = mBinding!!
    private val selectedButtons = mutableSetOf<Button>() // 현재 선택된 버튼들을 추적

    private lateinit var postViewModel: PostViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var postListAdapter: PostListAdapter

    private var tagName: String = ""
    private var keyword: String? = ""

    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageButton

    private var filters = mutableMapOf(
        "gender" to "m",
        "memberId" to "F_1"
        // 초기 필터 값 설정
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MateFragmentPostListBinding.inflate(inflater, container, false)
        recyclerView = binding.postList
        recyclerView.layoutManager = LinearLayoutManager(context)

        // ViewModel 초기화
        postViewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        mBinding = binding

        // searchInput과 searchButton 초기화
        searchInput = binding.searchInput
        searchButton = binding.searchButton

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 글쓰기 버튼 클릭 이벤트 설정
        val writeButton: Button = binding.writeButton

        writeButton.setOnClickListener {
            findNavController().navigate(R.id.action_postListFragment_to_postListWriteFragment)
        }

        // ballon 말풍선
        val balloon = createBalloon(requireContext()) {
            setArrowSize(10)
            setWidth(BalloonSizeSpec.WRAP)
            setHeight(BalloonSizeSpec.WRAP)
            setArrowOrientation(ArrowOrientation.RIGHT)
            setText("매칭글을 작성해보세요. 맞춤형 동행 메이트를 구할 수 있습니다!")
            setTextColorResource(R.color.black)
            setTextSize(10f)
            setBackgroundColorResource(R.color.super_light_gray)
            setBalloonAnimation(BalloonAnimation.ELASTIC)
            setLifecycleOwner(lifecycleOwner)
            setPadding(8)
        }

        writeButton.post {
            balloon.showAlignLeft(writeButton)
            balloon.dismissWithDelay(3000)
        }

        // ViewModel의 데이터 사용
        postViewModel.filterData?.let { filterData ->
            Log.i("filter", "Received filter data: $filterData")

            filterData.matchDate?.let { matchDate ->
                filters["matchDate"] = matchDate
            }
            filterData.matchBranch?.let { matchBranch ->
                filters["matchBranch"] = matchBranch
            }
            filterData.matchGender?.let { matchGender ->
                filters["matchGender"] = matchGender
            }
            filterData.matchLanguage?.let { matchLanguage ->
                filters["matchLanguage"] = matchLanguage
            }
            filterData.keyword?.let { keyword ->
                filters["keyword"] = keyword
            }
            filterData.tagName?.let { tagName ->
                filters["tagName"] = tagName
            }

            Log.i("filter", "Updated filters: $filters")
        }

        postViewModel.getPostList(filters, onSuccess = { postList ->
            postListAdapter = PostListAdapter(postList) { postId ->
                val action = PostListFragmentDirections.actionPostListFragmentToPostListDetailFragment(postId)
                findNavController().navigate(action)
            }
            recyclerView.adapter = postListAdapter
        }, onError = { errorMessage ->
            // 에러 처리
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })

        val toolbar = mBinding?.toolbar?.toolbar

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            title.setText("매칭글 목록")

            // 버튼의 가시성 설정
            val showLeftButton = false
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
        }

        // 필터 버튼 가져오기
        val filterButton: Button = binding.filterButton

        // 필터 버튼 클릭 이벤트 설정
        filterButton.setOnClickListener {
            findNavController().navigate(R.id.action_postListFragment_to_postListFilterFragment)
        }

        // buttonContainer 레이아웃을 가져오기
        val buttonContainer = binding.buttonContainer
        // view.findViewById<LinearLayout>(R.id.button_container)

        // 동적으로 버튼 추가
        val buttonsData = listOf("의류", "뷰티", "악세서리", "신발류", "한식", "일식", "양식", "중식", "분식", "팝업스토어", "전시", "공연") // 서버나 데이터베이스에서 가져온 데이터

        for (buttonText in buttonsData) {
            val button = Button(ContextThemeWrapper(requireContext(), R.style.TagButtonUnselected), null, R.style.TagButtonUnselected)
            button.text = buttonText
            val params = LinearLayout.LayoutParams(
                180,
                70
            )
            // 버튼 간의 간격을 설정 (예: 8dp)
            params.setMargins(8, 0, 8, 0)
            button.layoutParams = params
            button.gravity = Gravity.CENTER
            button.setPadding(16, 8, 16, 8)
            button.setBackgroundResource(R.drawable.tag_button_unselected)
            button.setTextColor(resources.getColor(R.color.dark_gray, null))

            // 버튼 추가 후 초기 상태 설정
            // 선택된 버튼 상태로 설정
            filters["tagName"]?.let { tagName ->
                val selectedTags = tagName.split(", ")
                if (selectedTags.contains(buttonText)) {
                    button.setBackgroundResource(R.drawable.tag_button_selected)
                    button.setTextColor(resources.getColor(R.color.white, null))
                    selectedButtons.add(button)
                }
            }

            // 버튼 클릭 이벤트 설정
            button.setOnClickListener {
                handleButtonClick(button)
            }

            buttonContainer.addView(button)
        }

        // 검색 버튼 클릭 이벤트 설정
        searchButton.setOnClickListener {
            performSearch()
        }
    }

    private fun handleButtonClick(clickedButton: Button) {
        if (selectedButtons.contains(clickedButton)) {
            // 선택 해제
            clickedButton.setBackgroundResource(R.drawable.tag_button_unselected)
            clickedButton.setTextColor(resources.getColor(R.color.dark_gray, null))
            selectedButtons.remove(clickedButton)
        } else {
            // 선택
            clickedButton.setBackgroundResource(R.drawable.tag_button_selected)
            clickedButton.setTextColor(resources.getColor(R.color.white, null)) // 선택된 버튼의 글자색을 흰색으로 변경
            selectedButtons.add(clickedButton)
        }

        updatePostList()
    }

    private fun performSearch() {
        val input = searchInput.text.toString().trim()

        // keyword가 빈 문자열일 경우 null로 설정
        keyword = if (input.isEmpty()) null else input

        updatePostList()
    }

    private fun updatePostList() {
        // 전역 필터에 tagName 추가 또는 업데이트
        tagName = selectedButtons.joinToString(", ") { it.text.toString() }
        Log.d("tag", "Data updated: $tagName")
        filters["tagName"] = tagName

        // 전역 필터에 keyword 추가 또는 업데이트
        if (keyword != null && keyword!!.isNotEmpty()) {
            filters["keyword"] = keyword!!
        } else {
            filters.remove("keyword")
        }

        postViewModel.updateFilterData(
            matchDate = filters["matchDate"],
            matchBranch = filters["matchBranch"]?.split(", ")?.toSet() ?: emptySet(),
            matchGender = filters["matchGender"],
            matchLanguage = filters["matchLanguage"]?.split(", ")?.toSet() ?: emptySet(),
            keyword = keyword.takeIf { !it.isNullOrBlank() },
            tagName = tagName.takeIf { it.isNotBlank() }
        )

        // 데이터 가져오기 및 Adapter 설정
        postViewModel.getPostList(filters, onSuccess = { postList ->
            postListAdapter = PostListAdapter(postList) { postId ->
                val action = PostListFragmentDirections.actionPostListFragmentToPostListDetailFragment(postId)
                findNavController().navigate(action)
            }
            recyclerView.adapter = postListAdapter
        }, onError = { errorMessage ->
            // 에러 처리
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}