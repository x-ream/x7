package io.xream.x7.demo.controller;

import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.CriteriaBuilder;
import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.common.web.ViewEntity;
import io.xream.x7.demo.CatRepository;
import io.xream.x7.demo.bean.CatEgg;
import io.xream.x7.demo.service.CatService;
import io.xream.x7.repository.TemporaryRepository;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categg")
public class CatEggController {

    @Autowired
    private TemporaryRepository temporaryRepository;
    @Autowired
    private CatRepository catRepository;
    @Autowired
    private CatService catService;

    @RequestMapping("/test")
    public ViewEntity test(){
        boolean flag = this.temporaryRepository.createRepository(CatEgg.class);
        System.out.println(flag);

        CatEgg catEgg = new CatEgg();
        catEgg.setId(1);
        catEgg.setCatId(2);
        catEgg.setName("test");

        this.temporaryRepository.create(catEgg);

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.resultWithDottedKey().resultKey("c.id").resultKey("c.type");
        builder.withoutOptimization().sourceScript("FROM cat c inner join catEgg e on e.catId = c.id");

        Criteria.ResultMappedCriteria resultMappedCriteria = builder.get();

        this.catRepository.findToHandle(resultMappedCriteria, map -> {

            Long id = MapUtils.getLong(map,"c.id");
            String catType = MapUtils.getString(map,"c.type");

            try {
                //service | fallback, not suggest to use transaction
                this.catService.refresh(
                        RefreshCondition.build().refresh("type", "NNLL").eq("id", id)
                );
            }catch (Exception e){

            }

        });

        this.temporaryRepository.dropRepository(CatEgg.class);

        return ViewEntity.ok();
    }
}
