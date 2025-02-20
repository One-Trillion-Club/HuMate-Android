package com.otclub.humate.mission.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otclub.humate.R
import com.otclub.humate.mission.data.ClearedMissionDTO

/**
 * 완료된 활동 Adapter
 * @author 손승완
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01 	손승완        최초 생성
 * 2024.08.06   손승완        다국어 처리
 * </pre>
 */
class ClearedMissionAdapter(
    private val clearedMissionList: List<ClearedMissionDTO>,
    private val languageCode: Int,
    private val onItemClick: (ClearedMissionDTO) -> Unit
) : RecyclerView.Adapter<ClearedMissionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mission_cleared_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context // Context 얻기
        val mission = clearedMissionList[position]
        if (languageCode == 1) {
            holder.missionTitle.text = mission.titleKo
        } else {
            holder.missionTitle.text = mission.titleEn
        }

        holder.missionStatus.apply {
            text = when (mission.status) {
                0 -> {
                    setBackgroundResource(R.drawable.mission_pending)
                    context.getString(R.string.status_ongoing)
                }
                else -> {
                    setBackgroundResource(R.drawable.mission_completed)
                    context.getString(R.string.status_completed)
                }
            }
        }
        // 이미지 URL이 있는 경우 Glide로 이미지 로딩
        if (mission.imgUrl?.isNotEmpty() == true) {
            Glide.with(holder.itemView.context)
                .load(mission.imgUrl)
                .into(holder.missionImage)
        }

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClick(mission)
        }
    }

    override fun getItemCount(): Int {
        return clearedMissionList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val missionImage: ImageView = itemView.findViewById(R.id.missionImage)
        val missionTitle: TextView = itemView.findViewById(R.id.missionTitle)
        val missionStatus: TextView = itemView.findViewById(R.id.missionStatus)
    }
}