package me.rumbugen.sevdesk.objects

/**
 * Represents a page of results in a paginated API response.
 *
 * This class provides information about the current page, its items, and navigation methods
 * to retrieve the next or previous page of results. It supports scenarios where the total
 * number of items is known (countAll=true) or unknown.
 *
 * @param items The list of items contained in this page.
 * @param limit The maximum number of items that can be returned in a single page.
 * @param offset The index of the first item in this page within the entire result set.
 * @param total The total number of items available across all pages.  If `null`, indicates that the total count is unknown.
 */
abstract class Page<T>(
    open val items: List<T>,
    open val limit: Int,
    open val offset: Int,
    open val total: Int?
) {
    open fun hasNext(): Boolean {
        return when {
            total != null -> offset + limit < total!!
            else -> items.size == limit
        }
    }

    open fun hasPrevious(): Boolean = offset > 0

    val currentPage: Int get() = (offset / limit) + 1
    val totalPages: Int? get() = total?.let { (it + limit - 1) / limit }

    abstract suspend fun next(): Page<T>
    abstract suspend fun previous(): Page<T>
}