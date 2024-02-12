package com.halawa.goodseasons.dao;

import java.util.List;

import com.halawa.goodseasons.model.entity.ImportedDataItem;

public interface DataImporterDao {

	List<ImportedDataItem> loadNextItems(int start, int pageSize);
}
