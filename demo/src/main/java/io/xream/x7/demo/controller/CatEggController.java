package io.xream.x7.demo.controller;

import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.CriteriaBuilder;
import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.common.web.ViewEntity;
import io.xream.x7.demo.CatRepository;
import io.xream.x7.demo.bean.CatEgg;
import io.xream.x7.repository.TemporaryRepository;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categg")
public class CatEggController {

    @Autowired
    private TemporaryRepository.Parser temporaryRepositoryParser;
    @Autowired
    private TemporaryRepository temporaryRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CatRepository catRepository;

    @RequestMapping("/test")
    public ViewEntity test(){
        String sql = this.temporaryRepositoryParser.parseAndGetSql(CatEgg.class);
        System.out.println(sql);

        this.jdbcTemplate.execute(sql);

        CatEgg catEgg = new CatEgg();
        catEgg.setId(1);
        catEgg.setCatId(1);
        catEgg.setName("test");

        boolean flag = this.temporaryRepository.create(catEgg);

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.sourceScript("FROM cat c inner join catEgg e on e.catId = c.id");

        Criteria.ResultMappedCriteria resultMappedCriteria = builder.get();

        this.catRepository.findToHandle(resultMappedCriteria, map -> {

            Long id = MapUtils.getLong(map,"id");
            String catType = MapUtils.getString(map,"type");

            this.catRepository.refresh(
                    RefreshCondition.build().refresh("type",catType).eq("id",id)
            );

        });

        return ViewEntity.ok();
    }
}
