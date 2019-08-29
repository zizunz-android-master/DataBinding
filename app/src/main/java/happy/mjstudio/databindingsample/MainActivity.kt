package happy.mjstudio.databindingsample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import happy.mjstudio.databindingsample.databinding.ActivityMainBinding
import happy.mjstudio.databindingsample.databinding.ItemRecyclerviewBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding : ActivityMainBinding

    /**
     * 그냥 String 객체
     */
    var text1 : String = "String"

    /**
     * Observable 객체
     */
    var text2 : ObservableField<String> = ObservableField("ObservableField<String>")

    /**
     * LiveData 객체
     */
    var text3 : MutableLiveData<String> = MutableLiveData<String>().apply{ this.value = "LiveData<String>" }

    /**
     * LiveData 리스트 객체
     */
    var texts: MutableLiveData<List<String>> =
        MutableLiveData<List<String>>().apply { this.value = (1..100).map { it.toString() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        mBinding.lifecycleOwner = this
        mBinding.activity = this

        mBinding.recyclerView.adapter = MyAdapter()


        mBinding.setVariable(BR.josoomin,"Hi")

//        mBinding.josoomin = "Hi"
    }

    fun onClickChangeButton(i : Int) {
        val randomString = Random.nextInt(0,1000).toString()

        when(i) {
            1->text1 = randomString
            2->text2.set(randomString)
            3->text3.value = randomString
        }
    }

    fun onClickRemoveFirstItemButton() {
        try {
            texts.value = texts.value!!.minus(texts.value!!.first())
        }catch (e : NoSuchElementException) {
            Log.e("ERROR",e.message)
        }
    }

}

class MyAdapter : RecyclerView.Adapter<MyAdapter.MyHolder>() {

    /**
     * RecyclerView Adapter의 아이템
     */
    var items: List<String> = listOf()

    /**
     * 아이템 레이아웃의 ViewDataBinding 객체를 만들어서 뷰 홀더의 인자로 넣어 반환
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecyclerviewBinding.inflate(inflater, parent, false)

        return MyHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) = holder.bind(items[position])


    /**
     * 기존에 View를 인자로 받던 것을 ViewDataBinding 객체를 인자로 받음
     *
     * .root 를 하면 해당 바인딩 객체의 View 객체를 얻어올 수 있음
     */
    inner class MyHolder(private val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * 바인딩객체에 item 이라는 이름을 가진 변수에 아이템을 넣어줌
         *
         * 자동 형변환이 되기 때문에 Any를 전달해도 됨
         */
        fun bind(item: Any) {
            binding.setVariable(BR.item, item)
            /**
             * 데이터에 따른 UI 업데이트가 밀려있다면 바로 실행되게끔 하는 메서드
             */
            binding.executePendingBindings()
        }
    }
}

/**
 * 우리의 커스텀 바인딩 어댑터를 정의해서 레이아웃 xml에서 사용할 수 있게함
 */
@BindingAdapter("app_recyclerview_items")
fun RecyclerView.setItems(items: List<String>) {
    (adapter as? MyAdapter)?.run {
        this.items = items
        this.notifyDataSetChanged()
    }
}