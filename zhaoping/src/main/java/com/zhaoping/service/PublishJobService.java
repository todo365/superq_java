/**
 * 
 */
package com.zhaoping.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.zhaoping.framework.mongodb.CMongoQuey;
import com.zhaoping.framework.mongodb.Condition;
import com.zhaoping.model.City;
import com.zhaoping.model.MapPoint;
import com.zhaoping.model.Province;
import com.zhaoping.model.Result;
import com.zhaoping.model.company.JobChance;

/**
 * @author hongxiao.shou
 *
 */
@Repository

public class PublishJobService implements IPublishJobService {
	private final String jobstablename = "jobs";

	@Resource
	CMongoQuey mongoQuey;

	/*
	 * 发布一个职位信息
	 */
	@Override
	public Result insertPublishJob(JobChance jobChance) {
		Result result = new Result();
		mongoQuey = mongoQuey.SelectCollection(jobstablename);
		int id = mongoQuey.maxID();
		jobChance.setId(id);
		int r = mongoQuey.insert(jobChance);
		if (r >= 0) {
			// 这里更新地理位置信息

			result.setCode(1);
		} else {
			result.setCode(-1);
			result.setInfo("发布职位失败");
		}
		return result;
	}

	/*
	 * 更新一个职位信息
	 * 
	 * @see
	 * com.zhaoping.service.IPublishJob#updatePubLishJob(com.zhaoping.model.
	 * company.JobChance)
	 */
	@Override
	public Result updatePubLishJob(JobChance jobChance) {
		Result result = new Result();
		mongoQuey = mongoQuey.SelectCollection(jobstablename);
		int r = mongoQuey.where("id", jobChance.getId()).update(jobChance);
		if (r != -1) {
			result.setCode(1);
		} else {
			result.setCode(-1);
			result.setInfo("更新职位失败");
		}
		return result;
	}

	/*
	 * 获得一个职位信息
	 */
	@Override
	public JobChance getdatePubLishById(int jobid) {
		List<JobChance> list1 = null;
		JobChance jobChance = new JobChance();
		jobChance.setCity(new City());
		jobChance.setProvince(new Province());
		jobChance.setJobType(1);
		// jobChance.setLowSalary(1);
		jobChance.setMapPoint(new MapPoint());
		// JobLabel jobLabel = new JobLabel();
		// jobLabel.setId(1);
		// jobLabel.setJobLabelName("包吃住");
		// jobChance.setLabels(jobLabel);

		mongoQuey = mongoQuey.SelectCollection(jobstablename);
		list1 = mongoQuey.where("id", jobid).select(JobChance.class);
		if (list1.size() >= 1) {
			return list1.get(0);
		}
		return jobChance;
	}

	/**
	 * 已经发布的职位
	 */
	@Override
	public List<JobChance> getDeliverListBycompanyId(int companyId) {
		mongoQuey = mongoQuey.SelectCollection(jobstablename);
		List<JobChance> list = mongoQuey.where("companyId", companyId).select(
				JobChance.class);
		return list;
	}

	/**
	 * 根据一组坐标拿到一组职位机会
	 */
	@Override
	public List<JobChance> getDeliverListByxy(MapPoint mapPoint) {
		mongoQuey = mongoQuey.SelectCollection(jobstablename);
		Double[] li = null;
		if (mapPoint.getMapX() != null)
			li = new Double[] { mapPoint.getMapX(), mapPoint.getMapY() };
		else {
			li = new Double[] { 116.420098, 39.911458 };
		}
		List<JobChance> list = mongoQuey.where("mapPoint", Condition.NEAR, li)
				.select(JobChance.class);
		return list;
	}

	/**
	 * 根据公司ids发的职位信息找到这个公司发布的职位信息
	 * 
	 * @param ids
	 * @return
	 */
	public List<JobChance> getDeliverListByids(List<Integer> ids) {
		mongoQuey = mongoQuey.SelectCollection(jobstablename);
		List<JobChance> list = mongoQuey.where("jobId", Condition.IN, ids)
				.select(JobChance.class);
		return list;
	}

}
